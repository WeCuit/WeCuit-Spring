package cn.wecuit.backen.utils;

import cn.wecuit.backen.utils.HTTP.HttpRequestConfig;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/11/2 16:43
 * @Version 1.0
 **/
public class OneLoginUtils {
    /**
     * 根据统一登录中心cookie获取学生处cookie
     *
     * @param tgc 统一登录中心cookie
     * @return
     */
    public static String login2XSC(String tgc){
        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig(3));
        http.addCookie("TGC", tgc, "sso.cuit.edu.cn");
        try {
            String html = http.doGet("http://xsc.cuit.edu.cn/JC/OneLogin.aspx");
            Pattern compile = Pattern.compile("window\\.location\\.href='(.*?)'");
            Matcher matcher = compile.matcher(html);
            if(matcher.find()) {
                String guidLink = matcher.group(1);
                http.doGet(guidLink);
                Map<String, String> cookie = http.getCookie();
                return cookie.get("CenterSoft");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
