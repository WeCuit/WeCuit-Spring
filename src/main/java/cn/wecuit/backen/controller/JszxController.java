package cn.wecuit.backen.controller;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.JszxService;
import cn.wecuit.backen.utils.CCUtil;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.RSAUtils;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/8/5 20:10
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Jszx")
@BaseResponse
public class JszxController {
    @Resource
    HttpServletRequest request;
    @Resource
    JszxService jszxService;
    @Value("${wecuit.ocr.server}")
    private String OCR_SERVER;

    @PostMapping("/getCheckInListV2")
    public Map<String, Object> getCheckInListV2(@RequestBody Map<String, String> body) throws IOException, ParseException {
        String cookie = body.get("cookie");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.0 Safari/537.36 Edg/84.0.521.0");
        headers.put("Cookie", cookie);

        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>() {{
            put("redirection", 0);
        }});
        HttpUtilEntity resp = http.doGetEntity("http://jszx-jxpt.cuit.edu.cn/Jxgl/Xs/netks/sj.asp?jkdk=Y", headers, "gb2312");
        if (resp.getStatusCode() != 200)
            throw new BaseException(20401, "计算中心还未登录");

        String html = resp.getBody();
        Map<String, List<Map<String, String>>> checkInList = CCUtil.parseCheckInList(html);
        return new HashMap<String, Object>() {{

            put("list", checkInList);
        }};
    }

    @RequestMapping("/loginRSAv1")
    public Map<String, Object> loginRSAv1(@RequestBody Map<String, String> uInfo) throws Exception {

        String userId = uInfo.get("userId");
        if (userId.length() > 15)
            userId = RSAUtils.decryptRSAByPriKey(userId);
        String userPass = RSAUtils.decryptRSAByPriKey(uInfo.get("userPass"));

        // 登录操作
        String loginCookie = CCUtil.login(userId, userPass);

        // 响应体
        Map<String, Object> ret = new HashMap<>();
        ret.put("cookie", loginCookie);
        return ret;
    }

    @RequestMapping("/getCheckInEditV2")
    public Map<String, Object> getCheckInEditV2(@RequestBody Map<String, String> body) throws IOException, ParseException {
        String cookie = body.get("cookie");
        String link = body.get("link");

        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>() {{
            put("redirection", 0);
        }});
        String reqUrl = "http://jszx-jxpt.cuit.edu.cn/Jxgl/Xs/netks/sjDb.asp?" + link;
        Map<String, String> headers = new HashMap<>();
        headers.put("cookie", cookie);
        HttpUtilEntity httpUtilEntity = http.doGetEntity(reqUrl, headers);
        String location = httpUtilEntity.getHeaders().get("Location");
        httpUtilEntity = http.doGetEntity("http://jszx-jxpt.cuit.edu.cn/Jxgl/Xs/netks/" + location, headers, "GB2312");
        if (302 == httpUtilEntity.getStatusCode()) throw new BaseException(20401, "未登录");

        String html = httpUtilEntity.getBody();
        Map<String, Object> form = CCUtil.parseCheckInContent(html);
        return new HashMap<String, Object>() {{
            put("form", form);
        }};
    }

    @PostMapping("/doCheckInV3")
    public Map<String, Object> doCheckInV3(@RequestBody Map<String, Object> postMap) throws IOException, ParseException {

        String cookie = (String) postMap.get("JSZXCookie");

        Map<String, String> form = (LinkedHashMap<String, String>) postMap.get("form");
        form = CCUtil.genPostBody(form, "?" + postMap.get("link"));

        Map<String, String> headers = new HashMap<>();
        headers.put("cookie", cookie);
        headers.put("referer", "http://jszx-jxpt.cuit.edu.cn/");

        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>() {{
            put("redirection", 0);
        }});
        String url = "http://jszx-jxpt.cuit.edu.cn/Jxgl/Xs/netks/editSjRs.asp";
        HttpUtilEntity httpUtilEntity = http.doPostEntity(url, form, headers, "GB2312");
        if (200 != httpUtilEntity.getStatusCode()) throw new BaseException(20401, "未登录");

        String html = httpUtilEntity.getBody();
        Pattern compile = Pattern.compile(">打卡时间：(.*?)</");
        Matcher matcher = compile.matcher(html);
        StringBuilder time = new StringBuilder();
        if (matcher.find()) {
            time.append(matcher.group(1));
        }

        if (html.contains("提交打卡成功！")) {
            Map<String, Object> newForm = CCUtil.parseCheckInContent(html);
            return new HashMap<String, Object>(){{
                put("msg", time.toString());
                put("form", newForm);
            }};
        } else {
            return new HashMap<String, Object>() {{
                put("code", 201);
                put("error", "失败了╮(╯▽╰)╭");
            }};
        }
    }

    @RequestMapping("/office_prepare")
    public Map<String, Object> officePrepare() throws IOException, ParseException {
        return jszxService.officePrepare();
    }

    @RequestMapping("/office_getCaptcha")
    public Map<String, Object> officeGetCaptcha(@RequestParam String cookie, @RequestParam String codeKey) throws IOException {

        HashMap<String, String> headers = new HashMap<String, String>() {{
            put("cookie", cookie);
            put("referer", "http://login.cuit.edu.cn:81/Login/xLogin/Login.asp");
        }};
        HttpUtil2 http = new HttpUtil2();
        byte[] body = http.getContent("http://login.cuit.edu.cn:81/Login/xLogin/yzmDvCode.asp?k=" + codeKey, null, headers, "UTF-8");
        Map<String, Object> ret = new HashMap<>();
        try {
            String s = http.doFilePost(OCR_SERVER, body);
            Map<String, String> map = JsonUtil.string2Obj(s, Map.class);
            ret.put("base64img", "data:image/png;base64, " + new String(Base64.getEncoder().encode(body)));
            ret.put("imgCode", map.get("result"));
        } catch (NoHttpResponseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @RequestMapping("/office_query")
    public Map<String, Object> officeQuery(@RequestBody Map<String, String> body) throws IOException, ParseException {
        String cookie = body.get("cookie");
        String codeKey = body.get("codeKey");
        String captcha = body.get("captcha");
        String nickname = body.get("nickname");
        String email = body.get("email");

        Map<String, String> param = new LinkedHashMap<String, String>() {{
            put("WinW", "1304");
            put("WinH", "768");
            put("txtId", nickname);
            put("txtMM", email);
            put("verifycode", captcha);
            put("codeKey", codeKey);
            put("Login", "Check");
            put("IbtnEnter.x", "8");
            put("IbtnEnter.y", "26");
        }};
        String msg = jszxService.officeQuery(param, cookie);
        return new HashMap<String, Object>() {{
            put("result", msg);
        }};
    }
}
