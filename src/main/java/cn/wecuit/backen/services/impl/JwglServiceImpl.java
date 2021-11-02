package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.JwglService;
import cn.wecuit.backen.utils.HTTP.HttpRequestConfig;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.JwcUtil;
import cn.wecuit.backen.utils.JwglUtil;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/9/11 14:15
 * @Version 1.0
 **/
@Service
public class JwglServiceImpl implements JwglService {
    @Override
    public String login(String cookie) throws IOException, ParseException {
        // 数据获取及准备
        String url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/home.action";
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("referer", "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36 Edg/90.0.818.49");

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        // 发送请求
        HttpUtilEntity resp = http.doGetEntity(url, headers);

        // 状态码处理
        if (resp.getStatusCode() != 200 && resp.getStatusCode() != 302)
            throw new BaseException(Integer.parseInt("13" + resp.getStatusCode()), "教务处异常" + resp.getStatusCode());

        String retCookie = "JSESSIONID=" + resp.getCookies().get("JSESSIONID") + "; GSESSIONID=" + resp.getCookies().get("GSESSIONID");

        // 检测WEBVPN是否登录
        String location = resp.getHeaders().get("Location");
        if (location != null && location.contains("//webvpn"))
            throw new BaseException(401, "WEBVPN未登录");

        // http://jwgl.cuit.edu.cn/eams/login.action;jsessionid=*****
        resp = http.doGetEntity(location, headers);

        // https://sso.cuit.edu.cn/authserver/login?service=***
        location = resp.getHeaders().get("Location");
        resp = http.doGetEntity(location, headers);

        // 	http://jwgl.cuit.edu.cn/eams/login.action;jsessionid=*****?ticket=****
        location = resp.getHeaders().get("Location");
        resp = http.doGetEntity(location, headers);

        // 	http://jwgl.cuit.edu.cn/eams/login.action;jsessionid=****
        location = resp.getHeaders().get("Location");
        resp = http.doGetEntity(location, headers);

        // Map<String, String> tmp_cookie = http.getCookie();
        return retCookie;
    }

    @Override
    public Map<String, Object> getGradeTable(String cookie) throws IOException, ParseException {
        Map<String, String> headers = new HashMap<>();
        headers.put("cookie", cookie);
        headers.put("Referer", "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/teach/grade/course/person!search.action?semesterId=302&projectType=");

        Map<String, String> body = new HashMap<String, String>() {{
            put("template", "report_latest_mode");
        }};

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        String url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/teach/grade/course/person!myHistory.action";
        HttpUtilEntity httpUtilEntity = http.doPostEntity(url, body, headers);
        if (200 != httpUtilEntity.getStatusCode())
            throw new BaseException(401, "似乎未登录");

        String html = httpUtilEntity.getBody();
        HashMap<String, Object> parseGradeTable = JwcUtil.parseGradeTable(html);

        String total = (String) parseGradeTable.get("total");
        total = total.replaceAll(" ", "");
        Pattern compile = Pattern.compile("\\d+\\.?\\d*");
        Matcher matcher = compile.matcher(total);
        List<String> totalList = new LinkedList<>();
        while (matcher.find())
            totalList.add(matcher.group());
        Map<String, String> totalMap = new HashMap<String, String>() {{
            put("learnTime", totalList.get(0));
            put("creditTotal", totalList.get(1));
            put("creditGet", totalList.get(2));
            put("point", totalList.get(3));
        }};
        return new HashMap<String, Object>() {{
            put("grade", parseGradeTable.get("v1"));
            put("total", totalMap);
        }};
    }

    @Override
    public Map<String, Object> getExamOption(String cookie) throws IOException, ParseException {
        Pattern compile = Pattern.compile("semester.id=(\\d+);");
        Matcher matcher = compile.matcher(cookie);
        String semester = "";
        if (matcher.find()) {
            semester = matcher.group(1);
        }

        // 考试类型
        Map<String, String> headers = new HashMap<String, String>() {{
            put("cookie", cookie);
        }};
        String url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/stdExamTable.action";

        HttpUtil2 httpUtil2 = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        HttpUtilEntity httpUtilEntity = httpUtil2.doGetEntity(url, headers);

        if (200 != httpUtilEntity.getStatusCode()) throw new BaseException(401, "教务处未登录");

        String html = httpUtilEntity.getBody();
        ;
        compile = Pattern.compile("<option value=\"(\\d+)\".*?>(.*?)</option>");
        matcher = compile.matcher(html);
        List<Map<String, String>> batch = new LinkedList<>();
        while (matcher.find()) {
            Matcher finalMatcher = matcher;
            batch.add(new HashMap<String, String>() {{
                put("id", finalMatcher.group(1));
                put("name", finalMatcher.group(2));
            }});
        }
        Map<String, String> cookie1 = httpUtil2.getCookie();
        if (null != cookie1.get("semester"))
            semester = cookie1.get("semester");

        // 激活
        httpUtil2.doGet("http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/teach/grade/course/person.action?_=1602123116051");
        headers.put("referer", "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/stdExamTable!examTable.action");
        headers.put("X-Requested-With", "XMLHttpRequest");
        String finalSemester = semester;
        Map<String, String> data = new HashMap<String, String>() {{
            put("tagId", "semesterBar17596100931Semester");
            put("dataType", "semesterCalendar");
            put("value", finalSemester);
            put("empty", "false");
        }};
        httpUtilEntity = httpUtil2.doPostEntity(
                "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/dataQuery.action?sf_request_type=ajax",
                data,
                headers
        );

        html = httpUtilEntity.getBody().replaceAll("\t|\n|\r", "").replaceAll("\\{(\\w+?):", "{\"$1\":").replaceAll(",(\\w+?):", ",\"$1\":");
        Map map = JsonUtil.string2Obj(html, Map.class);

        // 课表数据解析
        Map<String, ArrayList<Map<String, Object>>> sem = (Map<String, ArrayList<Map<String, Object>>>) map.get("semesters");

        // TODO: 粗暴的移除了2021-2022的数据
        sem.remove("y9");

        List<Map<String, Object>> s0 = new LinkedList<>();
        List<List<Map<String, Object>>> s1 = new LinkedList<>();
        Map<Object, Object> semesters = new LinkedHashMap<Object, Object>() {{
            put(0, s0);
            put(1, s1);
        }};
        sem.forEach((k, v) -> {
            final Map<String, Object> map0 = v.get(0);
            final Map<String, Object> map1 = v.get(1);
            s0.add(new HashMap<String, Object>() {{
                put("name", map0.get("schoolYear"));
            }});
            map0.remove("schoolYear");
            map1.remove("schoolYear");
            map0.put("name", "第" + map0.get("name") + "学期");

            map1.put("name", "第" + map1.get("name") + "学期");
            s1.add(v);
        });

        Collections.reverse(s0);
        Collections.reverse(s1);

        semesters.put("len", s1.size());
        map.put("yearIndex", -1 == Integer.parseInt((String) map.get("yearIndex")) ? 0 : s1.size() - 1 - Integer.parseInt((String) map.get("yearIndex")));
        map.put("semesters", semesters);

        String finalSemester1 = semester;
        return new HashMap<String, Object>() {{
            put("semesterCalendar", map);
            put("batch", batch);
            put("semester", finalSemester1);
        }};
    }

    @Override
    public Map<String, Object> getExamTable(String cookie, String batchId) throws IOException, ParseException {
        Map<String, String> headers = new HashMap<String, String>(){{
            put("cookie", cookie);
        }};

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        String html = http.doGet("http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/stdExamTable!examTable.action?examBatch.id=" + batchId, "GB2312", headers);
        if(!html.contains("考试地点"))throw new BaseException(401, "未登录");

        html = html.replaceAll("\n|\r|\r\n|<font color=\"BBC4C3\">|</font>|\t", "");

        Pattern compile = Pattern.compile("<td>(.*?)</td>");
        Matcher matcher = compile.matcher(html);
        String[] type = {"courseId",
                "courseName",
                "examType",
                "examDate",
                "examTime",
                "examSite",
                "credit",
                "examStatus",
                "remark"};
        int i = 0;
        Map<String, Object> table = new HashMap<>();
        List<Object> examList = new LinkedList<>();
        table.put("examList", examList);

        Map<String, String> temp = new HashMap<>();
        while (matcher.find()){
            String value = matcher.group(1);
            if(value.contains("href")){
                value = value.replaceFirst("<a[^>]*>(.*?)</a>", "$1");
            }else if(value.contains("sup")){
                value = value.replaceFirst("<sup[^>]*>(.*?)</sup>", "$1");
            }

            String key = type[i % type.length];
            temp.put(key, value);
            if(temp.size() == type.length){
                temp.putIfAbsent(key, "无");
                examList.add(temp);
                temp = new HashMap<>();
            }
            i++;
        }
        return table;
    }

    private static final Pattern courseOption = Pattern.compile("<option value=\"(.*?)\">(.*?)</option>");
    @Override
    public Map<String, Object> getCourseOption(String cookie) throws IOException, ParseException {
        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        String url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/courseTableForStd.action";
        Map<String, String> header = new HashMap<String, String>(){{
            put("cookie", cookie);
        }};
        HttpUtilEntity httpUtilEntity = http.doGetEntity(url, header);
        if(200 != httpUtilEntity.getStatusCode())throw new BaseException(401, "未登录");
        String semesterId = httpUtilEntity.getCookies().get("semester.id");
        header.put("cookie", "semester.id=" + semesterId + ";" + cookie);
        String body = httpUtilEntity.getBody();
        Matcher matcher = courseOption.matcher(body);
        int i = 0;
        List<Map<String, String>> courseType = new LinkedList<>();
        List<Map<String, String>> courseWeek = new LinkedList<>();
        while (matcher.find()){
            Map<String, String> t = new HashMap<String, String>(){{
                put("key", matcher.group(1));
                put("name", matcher.group(2));
            }};
            if(i++ < 2){
                courseType.add(t);
            }else{
                courseWeek.add(t);
            }
        }

        url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/teach/grade/course/person.action?_=1602123116051";
        http.doGetEntity(url, header);
        url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/dataQuery.action?sf_request_type=ajax";
        header.put("Referer", "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/stdExamTable!examTable.action");
        header.put("X-Requested-With", "XMLHttpRequest");
        Map<String, String> p = new HashMap<String, String>(){{
            put("tagId", "semesterBar17596100931Semester");
            put("dataType", "semesterCalendar");
            put("value", semesterId);
            put("empty", "false");
        }};
        httpUtilEntity = http.doPostEntity(url, p, header);
        Map<String, Object> semester = JwglUtil.parseTermList(httpUtilEntity.getBody());

        return new HashMap<String, Object>(){{
            put("courseType", courseType);
            put("courseWeek", courseWeek);
            put("semesters", semester);
        }};
    }

    private static Pattern courseTableP = Pattern.compile("if\\(jQuery\\(\"#courseTableType\"\\)\\.val\\(\\)==\"std\"\\)\\{.*?form\\.addInput\\(form,\"ids\",\"(\\d+)\".*?form\\.addInput\\(form,\"ids\",\"(\\d+)\"");
    @Override
    public Map<String, Object> getCourseTable(String cookie, String courseType) throws IOException, ParseException {
        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        String url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/courseTableForStd.action?_=1602082567619";
        Map<String, String> header = new HashMap<String, String>(){{
            put("cookie", cookie);
        }};
        HttpUtilEntity httpUtilEntity = http.doGetEntity(url, header);
        if(httpUtilEntity.getStatusCode()!=200)throw new BaseException(401, "未登录");

        Matcher matcher = courseTableP.matcher(httpUtilEntity.getBody().replaceAll("\r|\n|\r\n", ""));
        Map<String, String> ids = new HashMap<>();
        while (matcher.find()){
            ids.put("std", matcher.group(1));
            ids.put("class", matcher.group(2));
        }

        // 获取课表安排
        url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/courseTableForStd!courseTable.action?sf_request_type=ajax";
        String sid = httpUtilEntity.getCookies().get("semester.id");
        Map<String, String> p = new HashMap<String, String>(){{
            put("ignoreHead",  "1");
            put("setting.kind", courseType);
            put("startWeek", "");
            put("semester.id", sid);
            put("ids", ids.get(courseType));
        }};
        httpUtilEntity = http.doPostEntity(url, p, header);
        List<Object> course = JwglUtil.courseHandle(httpUtilEntity.getBody());

        // 获取校区
        String info = http.doGet2("http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/stdDetail.action", header);
        String position = info.contains("航空港")?"hkg":"lq";

        return new HashMap<String, Object>(){{
            put("location", position);
            put("classtable", course);
            put("start", getStartDateOfTerm());
        }};
    }
    private static final Pattern startDateP = Pattern.compile("datedifference\\(s1, '(\\d+)-(\\d+)-(\\d+)'\\);");
    private Map<String, String> getStartDateOfTerm() throws IOException, ParseException {

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        String body = http.doGet("https://jwc.cuit.edu.cn");
        Matcher matcher = startDateP.matcher(body);
        Map<String, String> date = new HashMap<>();
        if(matcher.find()){
            date.put("year", matcher.group(1));
            date.put("month", matcher.group(2));
            date.put("day", matcher.group(3));
        }
        return date;
    }
}
