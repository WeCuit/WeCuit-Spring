package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.utils.HTTP.HttpRequestConfig;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.JwcUtil;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/5 20:28
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Jwc")
@BaseResponse
public class JwcController {

    @PostMapping("/labAll")
    public Map<String, Object> labAll(@RequestBody Map<String, String> body) throws IOException, ParseException {

        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        String html = http.doGet("http://jxgl.cuit.edu.cn/Jxgl/Js/sysYxgl/Cx/sysHz.asp", body, "gb2312");

        //数据解析
        return JwcUtil.LAB_ListHtml2json(html);
    }

    @PostMapping("/labDetail")
    public Map<String, Object> labDetail(@RequestBody Map<String, String> body) throws IOException, ParseException {
        /*
            cxZtO: 8
            cxZcO:
            Kkxq: 20202                  ----学期
            Yx: 0                        ----院系
            Rw: 1                        ----承担任务
            Sys:                         ----实验室名称（可输入名称的一部分）
            Fj: HSZXB215                 ----房间（可只输入房间编号的左边部分）
            Jxb:                         ----教学班（可只输入班简名的左边部分）
            Zjjs:                        ----教师（可只输入姓名的左边部分）
            Jxkc:                        ----课程（可只输入课程名称的左边部分）
            cxZt: 8                      ----时间
            cxZc:                        ----周次
            Lb: 1                        ----类别
        */
        HttpUtil2 http = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(0);}});
        String html = http.doGet("http://jxgl.cuit.edu.cn/Jxgl/Js/sysYxgl/Cx/sysKb.asp", body, "gb2312");
        html = html.replace("请输入实验室名称", "").replace("<br>", "").replace("　　", " ");

        // 数据解析
        return JwcUtil.LAB_DetailHtml2json(html);
    }
}
