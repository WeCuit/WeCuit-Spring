package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.CardService;
import cn.wecuit.backen.utils.CardUtil;
import cn.wecuit.backen.utils.HTTP.HttpRequestConfig;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import cn.wecuit.backen.utils.URLUtil;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/14 21:38
 * @Version 1.0
 **/
@Service
public class CardServiceImpl implements CardService {
    @Override
    public Map<String, Object> login(String cookie) throws IOException, ParseException {
        Map<String, String> headers = new HashMap<>();
        headers.put("cookie", "TGC=" + cookie);

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        HttpUtilEntity httpUtilEntity = http.doGetEntity("https://sso.cuit.edu.cn/authserver/login?service=http%3a%2f%2fykt.cuit.edu.cn%3a12491%2flogin.aspx", headers);
        if (httpUtilEntity.getStatusCode() != 302) throw new BaseException(12401, "SSO未登录");
        String location = httpUtilEntity.getHeaders().get("Location");

        // http://ykt.cuit.edu.cn:12491/login.aspx?ticket=ST-18**********w-localhost
        httpUtilEntity = http.doGetEntity(location, headers);
        if (httpUtilEntity.getStatusCode() != 302) throw new BaseException(12401, "异常");
        location = httpUtilEntity.getHeaders().get("Location");
        Map<String, String> cookies = httpUtilEntity.getCookies();
        StringBuilder temp = new StringBuilder();
        cookies.forEach((k, v) -> {
            temp.append(k).append("=").append(v).append(";");
        });
        headers.put("cookie", temp.toString());

        // http://ykt.cuit.edu.cn:12491/login.aspx
        httpUtilEntity = http.doGetEntity(location, headers);
        location = httpUtilEntity.getHeaders().get("Location");
        if (!location.contains("getUserInfoById")) throw new BaseException(1, "失败");

        Map<String, String> urlQuery = URLUtil.getURLQuery(location);
        String idNo = urlQuery.get("IDNo");
        byte[] decode = Base64.getDecoder().decode(idNo);
        decode = Base64.getDecoder().decode(decode);
        decode = Base64.getDecoder().decode(decode);
        idNo = new String(decode);

        String finalIdNo = idNo;
        return new HashMap<String, Object>() {{
            put("AccNum", finalIdNo);
        }};
    }

    @Override
    public String getAccWallet(String accNum) throws IOException, ParseException {
        Map<String, String> param = new LinkedHashMap<>();
        param.put("AccNum", accNum);
        param.put("ContentType", "json");
        Map<String, String> sign = CardUtil.genSign(param, "AccWallet");
        param.putAll(sign);

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        return http.doGet("http://ykt.cuit.edu.cn:12490/QueryAccWallet.aspx", param);
    }

    @Override
    public String getDealRec(Map<String, String> d) throws IOException, ParseException {
        Map<String, String> sign = CardUtil.genSign(d, "DealRec");
        d.put("ContentType", "json");
        d.putAll(sign);

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        return http.doGet("http://ykt.cuit.edu.cn:12490/QueryDealRec.aspx", d);
    }
}
