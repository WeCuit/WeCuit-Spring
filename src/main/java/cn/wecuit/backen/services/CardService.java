package cn.wecuit.backen.services;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/14 21:37
 * @Version 1.0
 **/
public interface CardService {
    Map<String, Object> login(String cookie) throws IOException, ParseException;
    String getAccWallet(String accNum) throws IOException, ParseException;
    String getDealRec(Map<String, String> d) throws IOException, ParseException;
}
