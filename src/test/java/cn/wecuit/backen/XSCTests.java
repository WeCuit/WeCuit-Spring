package cn.wecuit.backen;

import cn.wecuit.backen.entity.XSC.CourseScore;
import cn.wecuit.backen.utils.FileUtil;
import cn.wecuit.backen.utils.XSCUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/11/2 19:37
 * @Version 1.0
 **/
public class XSCTests {
    /**
     * 解析 学生处->课程成绩排名管理 数据
     */
    @Test
    public void getStuCourseScore(){
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/XSC/StuCourseScore.html");
        String html = FileUtil.ReadFile(resourceAsStream);
        List<CourseScore> scoreList = XSCUtils.parseStuCourseScore(html);
        System.out.println(scoreList);
        Assertions.assertTrue(scoreList.size() > 0);
    }
}
