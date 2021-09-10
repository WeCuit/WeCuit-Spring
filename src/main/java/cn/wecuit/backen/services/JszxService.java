package cn.wecuit.backen.services;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/14 22:21
 * @Version 1.0
 **/
public interface JszxService {
    Map<String, Object> officePrepare() throws IOException, ParseException;
    String officeQuery(Map<String, String> param, String cookie) throws IOException, ParseException;
}
