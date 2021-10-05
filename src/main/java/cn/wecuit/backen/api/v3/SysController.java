package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.pojo.Option;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.OptionService;
import cn.wecuit.backen.services.MiniService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @ResponseBody
    @GetMapping("/maintenance/**")
    public String maintenance(){
        return "{\"errorCode\":10503,\"maintenance\":{\"BText\":\"暂停服务\",\"start\":1631154856,\"end\":1731154856,\"OText\":\"新版本正在开发中~\"}}";
    }

}
