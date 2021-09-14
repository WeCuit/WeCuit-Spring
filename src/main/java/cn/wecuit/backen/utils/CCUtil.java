package cn.wecuit.backen.utils;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import org.apache.hc.core5.http.ParseException;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/4/29 22:33
 * @Version 1.0
 *
 * 计算中心工具类
 **/
public class CCUtil {
    private static final Pattern checkInCompile = Pattern.compile("type=hidden name=wtOR_\\d value=\"(\\S*)\"");

    public static String login(String id, String pass) throws IOException, ParseException {
        return login(id, pass, null);
    }
    public static String login(String id, String pass, String cookie) throws IOException, ParseException {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.0 Safari/537.36 Edg/84.0.521.0");
        if(null != cookie) headers.put("cookie", cookie);
        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>(){{
            put("redirection", 0);
        }});
        //    loginPage
        HttpUtilEntity httpUtilEntity = http.doGetEntity("http://login.cuit.edu.cn/Login/xLogin/Login.asp", headers, "GB2312");

        String html = httpUtilEntity.getBody();

        JXDocument underTest = JXDocument.create(html);
        String xpath = "//*[@id=\"user_main\"]/ul/li[2]/div/ul[3]/li[2]/input[2]/@value";
        JXNode node = underTest.selNOne(xpath);
        // 验证码key
        String codeKey = node.asString();

        // 登陆页面获取的cookie
        String loginPageCookie = httpUtilEntity.getHeaders().get("Set-Cookie");
        if(null != loginPageCookie)
        {
            loginPageCookie = loginPageCookie.split(";")[0];
        }

        // 登录页面结束

        //模拟登录开始
        Map<String, String> params = new LinkedHashMap<>();
        params.put("WinW", "1304");
        params.put("WinH", "768");
        params.put("txtId", id);
        params.put("txtMM", pass);
        params.put("verifycode", "%B2%BB%B7%D6%B4%F3%D0%A1%D0%B4");
        params.put("codeKey", codeKey);
        params.put("Login", "Check");
        params.put("IbtnEnter.x", "31");
        params.put("IbtnEnter.y", "28");

        headers.put("Cookie", loginPageCookie);
        headers.put("Referer", "http://login.cuit.edu.cn/Login/xLogin/Login.asp");      // 不可少！

        httpUtilEntity = http.doPostEntity("http://login.cuit.edu.cn/Login/xLogin/Login.asp", params, headers, "GB2312");
        if(httpUtilEntity.getStatusCode() != 302){
            // 未跳转，登录失败
            underTest = JXDocument.create(httpUtilEntity.getBody());
            JXNode notice = underTest.selNOne("//*[@id=\"user_bottom\"]/ul/li[@class=\"user_main_z\"]/span/text()");
            throw new BaseException(20401, notice.asString());
        }

        // 操作另一个域名
        headers.put("Referer", "http://jxgl.cuit.edu.cn/");
        httpUtilEntity = http.doGetEntity("http://jszx-jxpt.cuit.edu.cn/jxgl/xs/netks/sj.asp?jkdk=Y", headers);
        Map<String, String> cookies = httpUtilEntity.getCookies();
        StringBuilder cookie1 = new StringBuilder();
        cookies.forEach((k,v)-> cookie1.append(k).append("=").append(v).append(";"));

        headers.put("cookie", cookie1.toString());
        String body = http.doGet2("http://jszx-jxpt.cuit.edu.cn/Jxgl/Login/tyLogin.asp", headers);

        Pattern compile = Pattern.compile(";URL=(.*)\">");
        Matcher matcher = compile.matcher(body);
        if(!matcher.find()){
            throw new BaseException(20401, "登录失败");
        }

        // 得到一个链接：http://login.cuit.edu.cn/Login/qqLogin.asp?Oid=jszx%2Djxpt%2Ecuit%2Eedu%2Ecn&OSid=*******
        // 这是绑定jszx-jxpt与login的关系吗？
        String url = matcher.group(1);
        headers.put("cookie", loginPageCookie);
        headers.put("referer", "http://jszx-jxpt.cuit.edu.cn/");
        http.doGet2(url, headers);

        // 从这里开始，似乎在激活cookie1
        headers.put("cookie", cookie1.toString());
        http.doGet2("http://jszx-jxpt.cuit.edu.cn/Jxgl/Login/tyLogin.asp", headers);
        http.doGet2("http://jszx-jxpt.cuit.edu.cn/Jxgl/Login/syLogin.asp", headers);
        http.doGet2("http://jszx-jxpt.cuit.edu.cn/Jxgl/UserPub/Login.asp?UTp=Xs&Func=Login", headers);

        /*
        loginPageCookie  -----> login.cuit.edu.cn
        cookie1          -----> jszx-jxpt.cuit.edu.cn
        */
        return loginPageCookie + ";" + cookie1;
    }

    // 解析打卡列表
    public static Map<String, List<Map<String,String>>> parseCheckInList(String html){

        JXDocument underTest = JXDocument.create(html);

        Map<String, List<Map<String,String>>> list = new HashMap<>();

        // 今天的
        List<Map<String,String>> today = new ArrayList<>();
        list.put("today", today);
        String xpath = "//body/div[2]/table/tbody/tr[2]/td[2]/..";
        JXNode jxNode = underTest.selNOne(xpath);
        // System.out.println(jxNode.asString());
        // System.out.println(jxNode.asElement().child(0).html());
        String status = "√".equals(jxNode.asElement().child(0).html()) ? "√" : "X";
        String title = jxNode.asElement().child(1).text();
        String link = jxNode.asElement().child(1).child(0).attr("href").substring(9);
        today.add(new HashMap<String, String>(){{
            put("title", title);
            put("status", status);
            put("link", link);
        }});

        // 过期的
        List<Map<String,String>> outDate = new ArrayList<>();
        list.put("outDate", outDate);
        List<JXNode> jxNodes = underTest.selN("//body/div[2]/table/tbody/tr[position()>2 and @valign=\"top\"]");
        for (JXNode node : jxNodes) {

            String status1 = "√".equals(node.asElement().child(0).html()) ? "√" : "X";
            String title1 = node.asElement().child(1).text();
            String link1 = node.asElement().child(1).child(0).attr("href").substring(9);
            outDate.add(new HashMap<String, String>(){{
                put("title", title1);
                put("status", status1);
                put("link", link1);
            }});
        }

        return list;
    }

    // 解析打卡内容
    public static Map<String, Object> parseCheckInContent(String html){
        JXDocument jxDocument = JXDocument.create(html);
        List<JXNode> jxNodes = jxDocument.selN("//body/form/div[2]/table/tbody/tr");

        // 标题
        JXNode jxNode = jxNodes.get(0).selOne("//tr/td/b/text()");
        String title = jxNode.asString();

        // 打卡时间
        jxNode = jxNodes.get(2).selOne("//tr/th[2]/table/tbody/tr/td/b/span/text()");
        String checkInTime = jxNode.asString();

        // 审核情况
        jxNode = jxNodes.get(4).selOne("//tr/td[2]/div/span/text()");
        String sh = jxNode.asString();

        // 匹配打卡内容
        Matcher checkInMatcher = checkInCompile.matcher(html);
        List<String[]> config = new LinkedList<>();
        while(checkInMatcher.find()){
            String[] split = checkInMatcher.group(1).split("\\\\\\|/");
            config.add(split);
        }

        // 手动
        Map<String, Object> form = new HashMap<String, Object>(){{
            put("title", title);
            put("checkTime", checkInTime);
        }};
        List<Map<String, Object>> data = new LinkedList<>();
        form.put("data", data);

        // 0
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "th1");
            put("lable", "1. ***个人健康现状***");
            put("inputType", "text");
            put("defaultValue", "");
            put("isRequired", false);
            put("disabled", true);
        }});
        //1
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21650_1");
            put("lable", "(1)现居住地点：");
            put("defaultIdx", config.get(0)[0]);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "航空港校内");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "龙泉校内");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "新气象小区");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 4);
                        put("name", "成信家园");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 5);
                        put("name", "成都(校外)");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 6);
                        put("name", "外地");
                    }},
            });
        }});
        //2
        data.add(new HashMap<String, Object>(){{
            put("type", "input");
            put("id", "sF21650_2");
            put("lable", "外地详址省：");
            put("inputType", "text");
            put("defaultValue", config.get(0)[1]);
            put("isRequired", false);
        }});
        //3
        data.add(new HashMap<String, Object>(){{
            put("type", "input");
            put("id", "sF21650_3");
            put("lable", "外地详址市：");
            put("inputType", "text");
            put("defaultValue", config.get(0)[2]);
            put("isRequired", false);
        }});
        //4
        data.add(new HashMap<String, Object>(){{
            put("type", "input");
            put("id", "sF21650_4");
            put("lable", "外地详址区（县）：");
            put("inputType", "text");
            put("defaultValue", config.get(0)[3]);
            put("isRequired", false);
        }});
        //5
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21650_5");
            put("lable", "(2)现居住地状态：");
            put("defaultIdx", config.get(0)[4]);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "一般地区");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "疫情防控重点地区");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "所在小区被隔离管控");
                    }}
            });

        }});
        //6
        data.add(new HashMap<String, Object>(){{

            put("type", "picker");
            put("id", "sF21650_6");
            put("lable", "(3)今天工作状态：");
            put("defaultIdx", config.get(0).length < 6 ? 0 : config.get(0)[5]);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "航空港校内上班或学习");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "龙泉校内上班或学习");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "在校外完成实习任务");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 4);
                        put("name", "在校外");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 5);
                        put("name", "在家");
                    }}
            });
        }});
        //7
        data.add(new HashMap<String, Object>(){{

            put("type", "picker");
            put("id", "sF21650_7");
            put("lable", "(4)个人健康状况：");
            put("defaultIdx", config.get(0).length < 7 ? 0 : config.get(0)[6]);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "正常");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "有新冠肺炎可疑症状");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "疑似感染新冠肺炎");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 4);
                        put("name", "确诊感染新冠肺炎");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 5);
                        put("name", "确诊感染新冠肺炎但已康复");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 6);
                        put("name", "有呕吐情况");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 7);
                        put("name", "有腹泻情况");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 8);
                        put("name", "有呕吐＋腹泻情况");
                    }}
            });
        }});
        //8
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21650_8");
            put("lable", "(5)个人生活状态：");
            put("defaultIdx", config.get(0).length < 8 ? 0 : config.get(0)[7]);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "正常");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "住院治疗");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "居家隔离观察");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 4);
                        put("name", "集中隔离观察");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 5);
                        put("name", "居家治疗");
                    }}
            });
        }});
        //9
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21650_9");
            put("lable", "(6)家庭成员状况：");
            put("defaultIdx", config.get(0).length < 9 ? 0 : config.get(0)[8]);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "全部正常");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "有人有可疑症状");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "有人疑似感染");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 4);
                        put("name", "有人确诊感染");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 5);
                        put("name", "有人确诊感染但已康复");
                    }}
            });
        }});
        //10
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "sF21650_10");
            put("lable", "(7)其他需要说明的情况：");
            String text = config.get(0).length > 9 ? config.get(0)[9] : "";
            put("defaultValue", text);
        }});

        // 申请进出学校(无需求则不填)---1
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "th2");
            put("lable", "2. ***申请进出学校(无需求则不填)***");
            put("inputType", "text");
            put("defaultValue", "注意：更改自动打卡时，本部分不会被记录");
            put("isRequired", false);
            put("disabled", true);
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "input");
            put("id", "sF21912_1");
            put("lable", "目的地：");
            put("inputType", "text");
            put("defaultValue", config.get(1)[0]);
            put("isRequired", false);
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "sF21912_2");
            put("lable", "事由：");
            String text = config.get(1).length>1?config.get(1)[1]:"";
            put("defaultValue", text);
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21912_3");
            put("lable", "计划(天)：");
            int idx = config.get(1).length>2?Integer.parseInt(config.get(1)[2]):0;
            put("defaultIdx", idx);
            put("isRequired", false);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "今天");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "明天");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "后天");
                    }}
            });
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21912_4");
            put("lable", "计划（时间）：");
            int idx = config.get(1).length>3?Integer.parseInt(config.get(1)[3]):0;
            idx = idx > 5 ? idx-5: 0 ;
            put("defaultIdx", idx);
            put("isRequired", false);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "06");
                        put("name", "06:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "07");
                        put("name", "07:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "08");
                        put("name", "08:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "09");
                        put("name", "09:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "10");
                        put("name", "10:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "11");
                        put("name", "11:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "12");
                        put("name", "12:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "13");
                        put("name", "13:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "14");
                        put("name", "14:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "15");
                        put("name", "15:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "16");
                        put("name", "16:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "17");
                        put("name", "17:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "18");
                        put("name", "18:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "19");
                        put("name", "19:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "20");
                        put("name", "20:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "21");
                        put("name", "21:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "22");
                        put("name", "22:00");
                    }}
            });
        }});
        //至
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21912_5");
            put("lable", "至（天）：");
            int idx = config.get(1).length>4?Integer.parseInt(config.get(1)[4]):0;
            idx = idx==9?idx:0;
            put("defaultIdx", idx);
            put("isRequired", false);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 1);
                        put("name", "当天");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 2);
                        put("name", "第2天");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 3);
                        put("name", "第3天");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", 9);
                        put("name", "下学期");
                    }}
            });
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21912_6");
            put("lable", "计划（时间）：");

            int idx = config.get(1).length>5?Integer.parseInt(config.get(1)[5]):0;
            idx = idx > 6 ? idx-6: 0 ;
            put("defaultIdx", idx);
            put("isRequired", false);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "07");
                        put("name", "07:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "08");
                        put("name", "08:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "09");
                        put("name", "09:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "10");
                        put("name", "10:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "11");
                        put("name", "11:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "12");
                        put("name", "12:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "13");
                        put("name", "13:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "14");
                        put("name", "14:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "15");
                        put("name", "15:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "16");
                        put("name", "16:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "17");
                        put("name", "17:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "18");
                        put("name", "18:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "19");
                        put("name", "19:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "20");
                        put("name", "20:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "21");
                        put("name", "21:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "22");
                        put("name", "22:00");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "23");
                        put("name", "23:00");
                    }}
            });
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "input");
            put("id", "sh");
            put("lable", "审核情况：");
            put("defaultValue", sh);
            put("isRequired", false);
            put("disabled", true);
        }});

        // 2
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "th3");
            put("lable", "3. ***最近14天以来的情况***");
            put("inputType", "text");
            put("defaultValue", "");
            put("isRequired", false);
            put("disabled", true);
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21648_1");
            put("lable", "(1)曾前往疫情防控重点地区？");
            put("defaultIdx", "Y".equals(config.get(2)[0])?1:2);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "Y");
                        put("name", "是");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "N");
                        put("name", "否");
                    }}
            });
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "sF21648_2");
            put("lable", "若曾前往，请写明时间、地点及简要事由：");
            put("defaultValue", config.get(2)[1]);
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21648_3");
            put("lable", "(2)接触过疫情防控重点地区高危人员？");
            put("defaultIdx", "Y".equals(config.get(2)[2])?1:2);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "Y");
                        put("name", "是");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "N");
                        put("name", "否");
                    }}
            });
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "sF21648_4");
            put("lable", "若接触过，请写明时间、地点及简要事由：");
            put("defaultValue", config.get(2)[3]);
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "picker");
            put("id", "sF21648_5");
            put("lable", "(3)接触过感染者或疑似患者？");
            put("defaultIdx", "Y".equals(config.get(2)[4])?1:2);
            put("isRequired", true);
            put("range", new HashMap[]{
                    new HashMap<String, Object>(){{
                        put("id", 0);
                        put("name", "-请选择-");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "Y");
                        put("name", "是");
                    }},
                    new HashMap<String, Object>(){{
                        put("id", "N");
                        put("name", "否");
                    }}
            });
        }});
        data.add(new HashMap<String, Object>(){{
            put("type", "textarea");
            put("id", "sF21648_6");
            put("lable", "若接触过，请写明时间、地点及简要事由：");
            String value = config.get(2).length>5?config.get(2)[5]:"";
            put("defaultValue", value);
        }});

        return form;
    }

    // 生成打卡数据
    public static Map<String, String> genPostBody(Map<String, String> form, String link){
        Map<String, String> query = URLUtil.getURLQuery(link);

        return new LinkedHashMap<String, String>(){{
            put("RsNum", "3");
            put("Id", query.get("Id"));
            put("Tx", "33_1");
            put("canTj", "1");
            put("isNeedAns", "0");
            put("UTp", query.get("UTp"));
            put("ObjId", query.get("ObjId"));
            // -------------个人健康现状-----------------------
            put("th_1", "21650");
            put("wtOR_1", "a\\|/a\\|/a\\|/a\\|/a\\|/a\\|/a\\|/a\\|/a\\|/a");
            put("sF21650_1", form.get("sF21650_1"));      // (1)现居住地点为
            put("sF21650_2", form.get("sF21650_2"));      // 外地详址[省]
            put("sF21650_3", form.get("sF21650_3"));      // 外地详址[市]
            put("sF21650_4", form.get("sF21650_4"));      // 外地详址[区(县)]
            put("sF21650_5", form.get("sF21650_5"));      // (2)现居住地状态
            put("sF21650_6", "0".equals(form.get("sF21650_6"))?"":form.get("sF21650_6"));      // (3)今天工作状态
            put("sF21650_7", form.get("sF21650_7"));      // (4)个人健康状况
            put("sF21650_8", form.get("sF21650_8"));      // (5)个人生活状态
            put("sF21650_9", form.get("sF21650_9"));      // (6)家庭成员状况
            put("sF21650_10", form.get("sF21650_10"));    // (7)其他需要说明的情况
            put("sF21650_N", "10");
            // -------------申请进出学校(无需求则不填)-----------------------
            put("th_2", "21912");
            put("wtOR_2", "a\\|/a\\|/a\\|/a\\|/a\\|/a");
            put("sF21912_1", form.get("sF21912_1"));     // 目的地
            put("sF21912_2", form.get("sF21912_2"));     // 事由
            put("sF21912_3", form.get("sF21912_3"));     // 出校[今/明/后]
            put("sF21912_4", form.get("sF21912_4"));     // 出校[几点]
            put("sF21912_5", form.get("sF21912_5"));     // 回校[当天/第2天/第3天/下学期]
            put("sF21912_6", form.get("sF21912_6"));     // 回校[几点]
            put("sF21912_N", "6");
            // -----------最近一个月以来的情况------------
            put("th_3", "21648");
            put("wtOR_3", "a\\|/666\\|/a\\|/666\\|/a\\|/666");
            put("sF21648_1", form.get("sF21648_1"));    // (1)曾前往疫情防控重点地区？
            put("sF21648_2", form.get("sF21648_2"));    // 若曾前往，请写明时间、地点及简要事由
            put("sF21648_3", form.get("sF21648_3"));    // (2)接触过疫情防控重点地区高危人员
            put("sF21648_4", form.get("sF21648_4"));    // 若接触过，请写明时间、地点及简要事由
            put("sF21648_5", form.get("sF21648_5"));    // (3)接触过感染者或疑似患者？
            put("sF21648_6", form.get("sF21648_6"));    // 若接触过，请写明时间、地点及简要事由
            put("sF21648_N", "6");
            // -----------从外地返校(预计，目前已在成都的不填)情况------------
            // "th_3" => "21649",
            // "wtOR_4" => "6\|/666\|/666\|/666",
            // "sF21649_1" => $form['sF21649_1'],        // 主要交通方式
            // "sF21649_2" => str2GBK($form['sF21649_2']),        // 公共交通的航班号、车次等
            // "sF21649_3" => $form['sF21649_3'],        // 返校（预计）时间[月]
            // "sF21649_4" => $form['sF21649_4'],        // 返校（预计）时间[日]
            // "sF21649_N" => "4",
            put("zw1", "");
            put("cxStYt", "A");
            put("zw2", "");
            put("B2", "提交打卡");
        }};
    }
}
