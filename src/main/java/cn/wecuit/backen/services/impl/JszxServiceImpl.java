package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.services.JszxService;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/8/14 22:22
 * @Version 1.0
 **/
@Service
public class JszxServiceImpl implements JszxService {

    @Override
    public Map<String, Object> officePrepare() throws IOException, ParseException {
        Map<String, String> headers = new HashMap<String, String>(){{
            put("referer", "http://login.cuit.edu.cn:81/Login/xLogin/Login.asp");
        }};
        HttpUtil2 http = new HttpUtil2();
        String html = http.doGet2("http://login.cuit.edu.cn:81/Login/xLogin/Login.asp", headers);
        Pattern compile = Pattern.compile("<input type=\"hidden\" name=\"codeKey\" value=\"(\\d+)\"");
        Matcher matcher = compile.matcher(html);
        String codeKey = "";
        if(matcher.find())
            codeKey = matcher.group(1);

        compile = Pattern.compile("<span style=\"color:#0000FF;\">(.*?)</span");
        matcher = compile.matcher(html);
        String syncTime = "";
        if(matcher.find())
            syncTime = matcher.group(1);
        Map<String, String> cookieMap = http.getCookie();
        StringBuilder cookie = new StringBuilder();
        cookieMap.forEach((k,v)->{
            cookie.append(k + "=" + v + ";");
        });

        String finalCodeKey = codeKey;
        String finalSyncTime = syncTime;
        return new HashMap<String, Object>(){{
            put("cookie", cookie.toString());
            put("codeKey", finalCodeKey);
            put("syncTime", finalSyncTime);
        }};
    }

    @Override
    public String officeQuery(Map<String, String> param, String cookie) throws IOException, ParseException {
        Map<String, String> headers = new HashMap<String, String>(){{
            put("cookie", cookie);
            put("referer", "http://login.cuit.edu.cn:81/Login/xLogin/Login.asp");
        }};

        HttpUtil2 http = new HttpUtil2();
        String html = http.doPost("http://login.cuit.edu.cn:81/Login/xLogin/Login.asp", param, headers, "GB2312");
        Pattern compile = Pattern.compile("class=user_main_z(.*?)</span");
        Matcher matcher = compile.matcher(html);

        StringBuilder result = new StringBuilder();

        if(matcher.find()){
            result.append(matcher.group(1));
        }
        return result.substring(result.lastIndexOf(">") + 1);
    }
}
