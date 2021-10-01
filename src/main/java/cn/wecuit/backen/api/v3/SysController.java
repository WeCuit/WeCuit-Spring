package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.pojo.Option;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.OptionService;
import cn.wecuit.backen.services.TencentService;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/6 5:46
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Sys")
@BaseResponse
public class SysController {
    @Resource
    TencentService tencentService;
    @Resource
    OptionService optionService;

    @RequestMapping("/getConfig")
    public Map<String, Object> getConfigAction() throws IOException {
        List<Option> miniConfig = optionService.getByPrefix("mini_");
        Map<String, Object> ret = new HashMap<>();
        Map<String, String> dict = new HashMap<String, String>() {{
            put("mini_index", "notice");
            put("mini_other_opensource", "github");
            put("mini_other_group", "group");
            put("mini_other_aboutlink", "about-link");
        }};
        miniConfig.forEach(config -> {
            String key = dict.get(config.getName());
            if (key != null)
                ret.put(key, config.getValue());
        });
        return ret;
    }

    /**
     * 获取用户信息
     * openid | 是否管理员[暂废]
     *
     * @throws IOException
     */
    @GetMapping("/getUserInfoV2")
    public Map<String, Object> getUserInfoV2Action(HttpServletRequest request, @RequestParam String code) throws IOException, ParseException {

        String referer = request.getHeader("referer");
        if (null == referer) throw new BaseException(20500, "请求异常");
        Map<String, Object> session;
        if (referer.contains("servicewechat.com"))
            session = tencentService.WX_code2session(code);
        else if (referer.contains("appservice.qq.com"))
            session = tencentService.QQ_code2session(code);
        else
            throw new RuntimeException("不支持的客户端");

        // 判断请求失败
        int errcode = (int)session.get("errcode");
        if(errcode != 0)throw new RuntimeException((String) session.get("errmsg"));

        Object openid = session.get("unionid");
        if(openid == null)openid = session.get("openid");
        Object finalOpenid = openid;
        return new HashMap<String, Object>() {{
            put("openid", finalOpenid);
        }};
    }

    @ResponseBody
    @GetMapping("/maintenance/**")
    public String maintenance(){
        return "{\"errorCode\":10503,\"maintenance\":{\"BText\":\"暂停服务\",\"start\":1631154856,\"end\":1731154856,\"OText\":\"新版本正在开发中~\"}}";
    }

}
