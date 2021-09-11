package cn.wecuit.backen.utils;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/5/1 19:28
 * @Version 1.0
 **/
public class JwcUtil {
    /**
     * 实验室html解析
     * @param html 实验室列表页面
     * @return
     */
    public static Map<String, Object> LAB_ListHtml2json(String html) {
        JXDocument jxDocument = JXDocument.create(html);

        // Form
        List<JXNode> jxNodes =
                jxDocument.selN("//body/div[1]/div/table/tbody/tr/td/form/input[@type=\"text\"]"
                        + "|//body/div[1]/div/table/tbody/tr/td/form/select");
        Map<String, Object> formInfo = new HashMap<>();
        jxNodes.forEach(o->{
            Element ele = o.asElement();
            if("input".equals(ele.tagName())){
                String name = ele.attr("name");
                String value = ele.attr("value");
                formInfo.put(name, value);
            }else if("select".equals(ele.tagName())){
                String name = ele.attr("name");
                List<Map<String, String>> select = new LinkedList<>();
                formInfo.put(name, select);
                ele.children()
                        .forEach(LambdaUtils.consumerWithIndex((child, index)->{
                    if(child.hasAttr("selected")){
                        formInfo.put(name + "_index", index);
                    }
                    String value = child.attr("value");
                    select.add(new HashMap<String, String>(){{
                        put("id", value);
                        put("text", child.text());
                    }});
                }));
            }
        });

        // list
        List<Map<String, Object>> list = new LinkedList<>();
        jxNodes = jxDocument.selN("//body/div[2]/table/tbody/tr/td/table/tbody/tr");
        if(!jxNodes.toString().contains("系统中目前没有符合查询条件的记录！"))
        jxNodes.forEach(node->{
            Map<String, Object> temp = new HashMap<>();
            List<Map<String, Object>> placeList = new LinkedList<>();
            temp.put("place", placeList);

            // 院系
            Element yx = node.asElement().child(0).child(0);
            String yx_link = yx.attr("href").substring(10);
            temp.put("name", yx.text());
            try {
                temp.put("link", URLDecoder.decode(yx_link, "GB2312"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // 校区|地点
            List<JXNode> places = node.sel("//table[@class='tabThin']/tbody/tr");
            places.forEach(place->{
                Element xq = place.asElement().child(0).child(0);
                String xq_name = xq.text();
                String xq_link = xq.attr("href").substring(10);

                Map<String, Object> place_temp = new HashMap<String, Object>(){{
                    put("name", xq_name);
                    try {
                        put("link", URLDecoder.decode(xq_link, "GB2312"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }};

                List<Map<String, String>> labList = new LinkedList<>();
                place_temp.put("lab",labList);
                List<JXNode> labs = place.sel("//table/tbody/tr/td/font/a");
                labs.forEach(lab->{
                    Element ele = lab.asElement();
                    String lab_link = ele.attr("href").substring(10);
                    String lab_style = lab.asElement().childNodeSize()>0 ? lab.asElement().childNode(0).attr("style") : "";


                    labList.add(new HashMap<String, String>(){{
                        put("name", ele.text());
                        try {
                            put("link", URLDecoder.decode(lab_link, "GB2312"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        put("style", lab_style);
                    }});
                });
                placeList.add(place_temp);
            });
            list.add(temp);
        });

        return new HashMap<String, Object>(){{
            put("form", formInfo);
            put("list", list);
        }};
    }

    /**
     * 实验室安排页面解析
     *
     * @param html 实验室安排页面
     * @return Map<String, Object>
     */
    public static Map<String, Object> LAB_DetailHtml2json(String html){
        JXDocument jxDocument = JXDocument.create(html);

        //Form
        List<JXNode> jxNodes =
                jxDocument.selN("//body/div[1]/div/table/tbody/tr/td/form/input[@type=\"text\"]"
                        + "|//body/div[1]/div/table/tbody/tr/td/form/select");
        Map<String, Object> formInfo = new HashMap<>();
        jxNodes.forEach(o->{
            Element ele = o.asElement();
            if("input".equals(ele.tagName())){
                String name = ele.attr("name");
                String value = ele.attr("value");
                formInfo.put(name, value);
            }else if("select".equals(ele.tagName())){
                String name = ele.attr("name");
                List<Map<String, String>> select = new LinkedList<>();
                formInfo.put(name, select);
                ele.children()
                        .forEach(LambdaUtils.consumerWithIndex((child, index)->{
                            if(child.hasAttr("selected")){
                                formInfo.put(name + "_index", index);
                            }
                            String value = child.attr("value");
                            select.add(new HashMap<String, String>(){{
                                put("id", value);
                                put("text", child.text());
                            }});
                        }));
            }
        });

        // Detail
        jxNodes = jxDocument.selN("//*[@id=\"wjTA\"]/tbody/tr[@valign=\"bottom\"]");
        Map<Integer, Object> list = new LinkedHashMap<>();
        jxNodes.forEach(tr->{
            String name = tr.asElement().child(0).text();
            Elements tds = tr.asElement().children();
            for (int i = 2; i < tds.size(); i++) {

                // 星期 $j / 2 , 节次 $name
                List<String> detail = LAB_DetailHtml2json_td(tds.get(i));
                if(null != detail)
                list.put(i / 2 - 1, new HashMap<String, List<String>>(){{
                    put(name, detail);
                }});
            }
        });
        return new HashMap<String, Object>(){{
            put("form", formInfo);
            put("list", list);
        }};
    }

    /**
     * 实验室安排页面解析 辅助方法
     * @param td 元素
     * @return List<String>
     */
    private static List<String> LAB_DetailHtml2json_td(Element td){
        List<String> p = new LinkedList<>();
        List<Node> nodes = td.childNodes();
        if(nodes.size() == 1)return null;
        // p标签
        nodes.forEach(item->{
            String trim = "";
            if(item instanceof TextNode)
            {
                trim = ((TextNode) item).text().trim();
            }else if(item instanceof Element){
                trim = ((Element) item).text().trim();
            }else{
                trim = item.toString().trim();
            }
            if(trim.length() > 0)p.add(trim);
        });
        return p;
    }

    public static HashMap<String, Object> parseGradeTable(String html){
        JXDocument jxDocument = JXDocument.create(html);

        //学生姓名
        String stdName = jxDocument.selNOne("//*[@id=\"stuBasicInfo\"]/tbody/tr[2]/td[4]").asElement().text();

        // 三、 学习成绩卡（各门课程最终成绩）
        List<JXNode> jxNodes = jxDocument.selN("//body/table[3]/tbody/tr[position()>1]");

        // 总成绩
        String total = jxNodes.get(0).asElement().text();

        // 移除总成绩
        jxNodes.remove(0);
        // 移除表头
        jxNodes.remove(0);

        List<Map<String, Object>> ret = new LinkedList<>();
        Map<String, Object> ret2 = new LinkedHashMap<>();

        // 自增整型
        AtomicInteger cnt = new AtomicInteger();

        jxNodes.forEach(node->{
            Elements tds = node.asElement().children();

            if(tds.size() == 1){
                // 学期标题 -----> 第2019-2020学年 第1学期 (*****)
                String semester = tds.get(0).text();

                // 取出标题数值
                String[] split = semester.split(" \\(");
                Pattern compile = Pattern.compile("\\d+\\.?\\d*");
                Matcher matcher = compile.matcher(split[1]);
                List<String> semester_info = new LinkedList<>();
                while(matcher.find()){
                    semester_info.add(matcher.group());
                }

                // 以学期为key
                if(cnt.get() > 0){
                    String text = (String)ret.get(cnt.get() - 1).get("text");
                    ret2.put(text, ret.get(cnt.get() - 1).get("data"));
                    ret.get(cnt.get() - 1).remove("text");
                }

                cnt.getAndIncrement();
                ret.add(new HashMap<String, Object>(){{
                    put("name", split[0]);
                    put("text", semester);
                    put("total", new HashMap<String, String>(){{
                        put("learnTime", semester_info.get(0));
                        put("creditTotal", semester_info.get(1));
                        put("creditGet", semester_info.get(2));
                        put("point", semester_info.get(3));
                    }});
                }});
            }else{
                // 成绩条目
                String name = tds.get(1).text();            // 名称
                String learnTime = tds.get(2).text();       // 学时
                String learnCredit = tds.get(3).text();     // 学分
                String lessonGrade = tds.get(4).text();     // 平时成绩
                String examGrade = tds.get(5).text();       // 考试成绩
                String learnGrade = tds.get(6).text();      // 总评成绩

                Map<String, Object> data;
                if(null == (data = (Map<String, Object>)ret.get(cnt.get() - 1).get("data"))) {
                    data = new HashMap<>();
                    ret.get(cnt.get() - 1).put("data", data);
                }

                data.put(name, new HashMap<String, String>(){{
                        put("learnTime", learnTime);
                        put("learnCredit", learnCredit);
                        put("lessonGrade", lessonGrade);
                        put("examGrade", examGrade);
                        put("learnGrade", learnGrade);
                    }});

                if(tds.size() > 8){
                    // 第二门
                    data.put(tds.get(8).text(), new HashMap<String, String>(){{
                        put("learnTime", tds.get(9).text());
                        put("learnCredit", tds.get(10).text());
                        put("lessonGrade", tds.get(11).text());
                        put("examGrade", tds.get(12).text());
                        put("learnGrade", tds.get(13).text());
                    }});
                }
            }
        });

        // 以学期为key
        ret2.put((String)ret.get(cnt.get() - 1).get("text"), ret.get(cnt.get() - 1).get("data"));
        ret.get(cnt.get() - 1).remove("text");

        Collections.reverse(ret);

        return new HashMap<String, Object>(){{
            put("v1", ret);
            put("v2", ret2);
            put("stdName", stdName);
            put("total", total);
        }};
    }
}
