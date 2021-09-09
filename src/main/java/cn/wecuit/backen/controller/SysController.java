package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.Option;
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
    HttpServletRequest request;
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
    public Map<String, Object> getUserInfoV2Action(@RequestParam String code) throws IOException, ParseException {

        Map<String, Object> session = tencentService.code2session(code, getClientId());

        return new HashMap<String, Object>() {{
            put("code", 200);
            put("info", new HashMap<String, Object>() {{
                put("isAdmin", false);
                put("openid", session.get("openid"));
            }});
        }};
    }

    @ResponseBody
    @GetMapping("/maintenance/**")
    public String maintenance(){
        return "{\"errorCode\":10503,\"maintenance\":{\"BText\":\"暂停服务\",\"start\":1631154856,\"end\":1731154856,\"OText\":\"新版本正在开发中~\"}}";
    }
    /**
     * @return 0 wx | 1 qq
     */
    public final int getClientId() {

        String referer = request.getHeader("referer");
        if (null == referer) throw new BaseException(20500, "请求异常");
        if (referer.contains("servicewechat.com")) return 0;
        else if (referer.contains("appservice.qq.com")) return 1;
        else
            throw new BaseException(20403, "不支持的客户端");
    }
}
