package cn.wecuit.backen.controller;

import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.response.ResponseResult;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.JwglService;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.JwcUtil;
import cn.wecuit.backen.utils.JwglUtil;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/8/5 20:33
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Jwgl")
@BaseResponse
public class JwglController {

    @Resource
    HttpServletRequest request;

    @Resource
    JwglService jwglService;

    @RequestMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> data) throws IOException, ParseException {

        String cookie = data.get("cookie");

        String login = jwglService.login(cookie);
        return new HashMap<String, Object>(){{
            put("cookie", login);
        }};
    }

    @RequestMapping("/loginCheck")
    public ResponseResult loginCheck() throws IOException, ParseException {
        String cookie = request.getParameter("cookie");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("referer", "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.68 Safari/537.36 Edg/86.0.622.31");
        headers.put("cookie", cookie);
        String home_url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/home.action";

        HttpUtil2 http = new HttpUtil2(new HashMap<String, Object>() {{
            put("redirection", 0);
        }});
        HttpUtilEntity resp = http.doGetEntity(home_url, headers);

        ResponseResult response = new ResponseResult();
        if(200 == resp.getStatusCode()){
            response.setCode(200);
        }else{
            response.setCode(401);
            response.setMsg("未登录");
        }
        return response;
    }

    @RequestMapping("/getGradeTableV2")
    public Map<String, Object> getGradeTableV2(@RequestBody Map<String, String> data) throws IOException, ParseException {
        String cookie = data.get("cookie");

        return jwglService.getGradeTable(cookie);
    }

    @RequestMapping("/getExamOption")
    public Map<String, Object> getExamOption(@RequestBody Map<String, String> d) throws IOException, ParseException {
        String cookie = d.get("cookie");
        return jwglService.getExamOption(cookie);
    }

    @RequestMapping("/getExamTable")
    public Map<String, Object> getExamTable(@RequestBody Map<String, String> d) throws IOException, ParseException {

        String cookie = d.get("cookie");
        String batchId = d.get("batchId");

        return jwglService.getExamTable(cookie, batchId);
    }

    @RequestMapping("/getCourseOption")
    public Map<String, Object> getCourseOption(@RequestBody Map<String, String> d) throws IOException, ParseException {
        String cookie = d.get("cookie");

        return jwglService.getCourseOption(cookie);
    }

    @RequestMapping("/getCourseTableV2")
    public Map<String, Object> getCourseTableV2(@RequestBody Map<String, Object> d) throws IOException, ParseException {
        String cookie = (String)d.get("cookie");
        String courseType = (String)d.get("courseType");

        return jwglService.getCourseTable(cookie, courseType);
    }


}
