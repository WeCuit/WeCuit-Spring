package cn.wecuit.backen.utils;

import cn.wecuit.backen.entity.XSC.CourseScore;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/11/2 19:16
 * @Version 1.0
 **/
public class XSCUtils {
    /**
     *  课程成绩排名管理
     *
     * @param centerSoft 学生处Cookie
     */
    public static void getStuCourseScore(String centerSoft){
        HttpUtil2 http = new HttpUtil2();
        try {
            http.addCookie("CenterSoft", centerSoft, "xsc.cuit.edu.cn");
            String html = http.doGet("http://xsc.cuit.edu.cn/Sys//SystemForm/StudentJudge/StuCourseScore.aspx");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static List< CourseScore> parseStuCourseScore(String html){
        // <td align="center" style="width:8%;">2019051233</td><td align="center" style="width:8%;">陈钱程</td><td align="center" style="width:8%;">86.69</td><td align="center" style="width:15%;">43/273</td><td>计算机196</td><td>2019</td><td>计算机科学与技术</td><td align="center" style="width:12%;">计算机学院</td><td align="center" style="width:5%;">否</td><td align="center" style="width:12%;">2020-2021学年</td>
        Pattern compile = Pattern.compile("<td .*?>(\\d+)</td><td .*?>(.*?)</td><td .*?>([0-9]+\\.?[0-9]+)</td><td .*?>(\\d+/?\\d+)</td><td>(.*?)</td><td>(\\d+)</td><td>(.*?)</td><td .*?>(.*?)</td><td .*?>(.)</td><td .*?>(\\d+-\\d+.*?)</td>");
        Matcher matcher = compile.matcher(html);
        List< CourseScore> result = new ArrayList<>();
        int i = 0;
        while (matcher.find()){
            CourseScore courseScore = new CourseScore(++i, matcher.group(1), matcher.group(2), Double.parseDouble(matcher.group(3)), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7), matcher.group(8), matcher.group(9), matcher.group(10));
            result.add(courseScore);
        }
        return result;
    }
}
