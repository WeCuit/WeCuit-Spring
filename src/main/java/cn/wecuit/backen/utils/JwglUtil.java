package cn.wecuit.backen.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/6/5 11:00
 * @Version 1.0
 **/
@Slf4j
public class JwglUtil {
    public static Map<String, Object> parseTermList(String body){
        String s = body.replaceAll("\\{(\\w+?):", "{\"$1\":").replaceAll(",(\\w+?):", ",\"$1\":");
        Map map = JsonUtil.string2Obj(s, Map.class);
        log.info("{}",map);
        Map<String, Object> sem = (Map<String, Object>) map.get("semesters");
        List<Map<String, Object>> schoolYear = new LinkedList<>();
        List<List<Map<String, Object>>> schoolSem = new LinkedList<>();
        List<Object> semester = new LinkedList<>();
        semester.add(schoolYear);
        semester.add(schoolSem);

        sem.forEach((y, list)->{
            List<Map<String, Object>> d = (List<Map<String, Object>>) list;
            Map<String, Object> year = new HashMap<String, Object>(){{
               put("name", d.get(0).get("schoolYear"));
            }};
            schoolYear.add(year);

            List<Map<String, Object>> semd = new LinkedList<>();
            for (Map<String, Object> item : d) {
                semd.add(new HashMap<String, Object>(){{
                    put("id", item.get("id"));
                    put("name", "第" + item.get("name") + "学期");
                }});
            }
            schoolSem.add(semd);
        });
        Collections.reverse(schoolYear);
        Collections.reverse(schoolSem);

        List<Integer> index = new LinkedList<Integer>(){{
            add(schoolYear.size() - 1 - Integer.parseInt((String)map.get("yearIndex")));
            add(Integer.parseInt((String)map.get("termIndex")));
        }};
        return new HashMap<String, Object>(){{
            put("list", semester);
            put("index", index);
        }};
    }

    private static Pattern courseHandleP_tinfo = Pattern.compile("var actTeachers = \\[\\{id:(\\d+),name:\"(.*?)\"(.*?)]");
    private static Pattern courseHandleP_linfo = Pattern.compile("actTeacherName\\.join\\(','\\),\".*?\",\"(.*?)\",\"\\d+\",\"(.*?)\",\"(\\d+)\",");
    private static Pattern courseHandleP_lplan = Pattern.compile("index =(\\d?)\\*unitCount\\+(\\d{0,2});");
    public static List<Object> courseHandle(String body){
        String[] split = body.replaceAll("\r|\n|\r\n", "").split("var teachers =");
        List<String> strings = new ArrayList<>(Arrays.asList(split));
        strings.remove(0);
        List<Object> course = new LinkedList<>();
        for (String string : strings) {
            // 教师 id & 名称
            Matcher tinfo = courseHandleP_tinfo.matcher(string);
            // 课程信息
            Matcher linfo = courseHandleP_linfo.matcher(string);
            // 课程安排
            Matcher lplan = courseHandleP_lplan.matcher(string);

            if(tinfo.find()&&linfo.find()&&lplan.find()){
                final int[] li = {1};
                Map<String, Object> temp = new HashMap<String, Object>(){{
                    put("teacherId", tinfo.group(1));
                    put("teacherName", tinfo.group(2));
                    put("name", linfo.group(1));
                    put("place", linfo.group(2));

                    put("day_of_week", Integer.parseInt(lplan.group(1)) + 1);
                    put("class_of_day", Integer.parseInt(lplan.group(2)) + 1);
                    if(lplan.find()) li[0]++;
                    put("duration", li[0]);
                }};

                // 处理上课周数
                String ltime = linfo.group(3);
                List<Integer> week_num = new LinkedList<>();
                temp.put("week_num", week_num);
                for (int i = 0; i < ltime.length(); i++) {
                    if('1' == (ltime.charAt(i)))
                        week_num.add(i);
                }

                course.add(temp);
            }

        }

        return course;
    }
}
