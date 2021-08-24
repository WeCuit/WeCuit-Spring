package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.ResponseData;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.utils.ApiUtil;
import cn.wecuit.backen.utils.CardUtil;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.URLUtil;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/5 16:40
 * @Version 1.0
 **/
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@RequestMapping("/Card")
public class CardController {
    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;

    @PostMapping("/login")
    public ResponseData login(@RequestBody Map<String, String> d) throws IOException, ParseException {
        String cookie = d.get("cookie");

        Map<String, String> headers = new HashMap<>();
        headers.put("cookie", "TGC=" + cookie);

        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>() {{
            put("redirection", 0);
        }});
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
        return new ResponseData() {{
            setCode(200);
            setData(new HashMap<String, String>() {{
                put("AccNum", finalIdNo);
            }});
        }};
    }

    @PostMapping("/getAccWallet")
    public ResponseData getAccWallet(@RequestBody Map<String, String> d) throws NoSuchAlgorithmException, IOException, ParseException {
        String accNum = d.get("AccNum");
        Map<String, String> param = new LinkedHashMap<>();
        param.put("AccNum", accNum);
        param.put("ContentType", "json");
        Map<String, String> sign = CardUtil.genSign(param, "AccWallet");
        param.putAll(sign);

        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>() {{
            put("redirection", 0);
        }});
        String result = http.doGet("http://ykt.cuit.edu.cn:12490/QueryAccWallet.aspx", param);

        ResponseData response = new ResponseData();

        Map map = JsonUtil.string2Obj(result, Map.class);
        String code = (String) map.get("Code");
        if ("1".equals(code)) {
            response.setCode(200);
            response.setData(map);
        } else {
            response.setCode(Integer.parseInt(code));
            response.setMsg((String) map.get("Msg"));
        }
        return response;
    }

    @PostMapping("/getDealRec")
    public ResponseData getDealRec(@RequestBody Map<String, String> d) throws IOException, ParseException, NoSuchAlgorithmException {
        Map<String, String> sign = CardUtil.genSign(d, "DealRec");
        d.put("ContentType", "json");
        d.putAll(sign);

        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>() {{
            put("redirection", 0);
        }});
        String result = http.doGet("http://ykt.cuit.edu.cn:12490/QueryDealRec.aspx", d);

        ResponseData response = new ResponseData();
        Map map = JsonUtil.string2Obj(result, Map.class);
        String code = (String)map.get("Code");
        if("1".equals(code)){
            response.setCode(200);
            response.setData(map);
        }else{
            response.setCode(Integer.parseInt(code));
        }
        return response;
    }
}
