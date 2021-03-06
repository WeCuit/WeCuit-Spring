package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.JwglService;
import cn.wecuit.backen.utils.HTTP.HttpRequestConfig;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String,Object> loginCheck(@RequestBody Map<String, String> body) throws IOException, ParseException {
        String cookie = body.get("cookie");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("referer", "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.68 Safari/537.36 Edg/86.0.622.31");
        headers.put("cookie", cookie);
        String home_url = "http://jwgl-cuit-edu-cn.webvpn.cuit.edu.cn:8118/eams/home.action";

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        HttpUtilEntity resp = http.doGetEntity(home_url, headers);

        Map<String, Object> result = new HashMap<>();
        if(200 == resp.getStatusCode()){
            result.put("login", true);
        }else{
            result.put("login", false);
        }
        return result;
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
