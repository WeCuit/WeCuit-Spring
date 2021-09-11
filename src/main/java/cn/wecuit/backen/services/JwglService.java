package cn.wecuit.backen.services;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/11 14:15
 * @Version 1.0
 **/
public interface JwglService {
    String login(String cookie) throws IOException, ParseException;
    Map<String, Object> getGradeTable(String cookie) throws IOException, ParseException;
    Map<String, Object> getExamOption(String cookie) throws IOException, ParseException;
    Map<String, Object> getExamTable(String cookie, String batchId) throws IOException, ParseException;
    Map<String, Object> getCourseOption(String cookie) throws IOException, ParseException;
    Map<String, Object> getCourseTable(String cookie, String courseType) throws IOException, ParseException;
}
