package cn.wecuit.backen.utils;

import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/5/15 16:43
 * @Version 1.0
 **/
public class TheolUtil {
    public static List<Map<String, String>> courseListHandle(String html){
        JXDocument jxDocument = JXDocument.create(html);
        List<JXNode> jxNodes = jxDocument.selN("//table[@id=\"table2\"]/tbody/tr");
        jxNodes.remove(0);

        List<Map<String,String>> list = new LinkedList<>();
        Pattern compile = Pattern.compile("courseId=(\\d+)");
        jxNodes.forEach(node -> {
            Matcher matcher = compile.matcher(node.asElement().children().get(0).html());
            if(matcher.find()) {
                Map<String, String> course = new HashMap<String, String>() {{
                    put("course", node.asElement().children().get(0).text());
                    put("college", node.asElement().children().get(1).text());
                    put("teacher", node.asElement().children().get(2).text());
                    put("courseId", matcher.group(1));
                }};
                list.add(course);
            }
        });

        return list;
    }

    public static Map<String, Object> dirTreeHandle(String xml){
        JXDocument jxDocument = JXDocument.create(xml);

        JXNode jxNode = jxDocument.selNOne("//root/item");
        return dirTreeHandle_item(jxNode);
    }
    private static Map<String, Object> dirTreeHandle_item(JXNode node){
        Map<String, Object> item = new LinkedHashMap<>();
        String id = node.asElement().attr("id");
        String text = node.selOne("/content/name/text()").toString();

        item.put("id", id);
        item.put("text", text);
        item.put("open", true);

        List<JXNode> childMenu = node.sel("/item");
        List<Map<String, Object>> childMenus = new LinkedList<>();
        childMenu.forEach(child-> childMenus.add(dirTreeHandle_item(child)));
        if(childMenus.size()>0)item.put("childMenus", childMenus);

        return item;
    }

    public static Map<String, Object> folderListHandle(String html){

        JXDocument jxDocument = JXDocument.create(html);
        List<JXNode> jxNodes = jxDocument.selN("//body/div/form/table/tbody/tr/td/..");

        List<Map<String, String>> folder = new LinkedList<>();
        List<Map<String, String>> file = new LinkedList<>();
        Map<String, Object> dir = new HashMap<>();
        jxNodes.forEach(tr->{
            Map<String, String> temp = new HashMap<>();
            Element a = tr.asElement().child(0).child(1);
            if(0 == tr.asElement().attributes().size()){
                // 目录
                temp.put("type", "folder");
                temp.put("text", a.text());
                String href = a.attr("href");
                Pattern compile = Pattern.compile("folderid=(\\d+)&lid=(\\d+)");
                Matcher matcher = compile.matcher(href);
                if(matcher.find()) {
                    temp.put("id", matcher.group(1));
                    temp.put("lid", matcher.group(2));
                    folder.add(temp);
                }
            }else{
                // 文件
                temp.put("type", "file");
                Map<String, String> type_dict = new HashMap<String, String>(){{
                    put("word", "doc");
                    put("powerpoint", "ppt");
                    put("excel", "xlsx");
                }};

                String img = tr.asElement().child(0).child(0).attr("src");
                Matcher matcher = Pattern.compile("/(\\w+)\\.").matcher(img);
                if(matcher.find()) {
                    String type = matcher.group(1);
                    temp.put("suffix", type_dict.get(type) != null ? type_dict.get(type) : type);
                }
                temp.put("text", a.text());
                String href = a.attr("href");
                matcher = Pattern.compile("fileid=(\\d+)&resid=(\\d+)&lid=(\\d+)").matcher(href);
                if(matcher.find()) {
                    temp.put("id", matcher.group(1));
                    temp.put("resId", matcher.group(2));
                    temp.put("lid", matcher.group(3));
                    temp.put("view", tr.asElement().child(1).text());
                    temp.put("download", tr.asElement().child(2).text());
                    file.add(temp);
                }
            }
        });
        if(folder.size()>0)dir.put("folder", folder);
        if(file.size()>0)dir.put("file", file);
        return dir;
    }

    public static String getFileType(String html){
        JXDocument jxDocument = JXDocument.create(html);
        JXNode jxNode = jxDocument.selNOne("//body/div/table/tbody/tr[12]/td/text()");
        String s = jxNode.toString();
        return s.substring(s.indexOf("application"));
    }
}
