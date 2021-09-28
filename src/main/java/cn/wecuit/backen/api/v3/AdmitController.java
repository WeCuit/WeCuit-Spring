package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.AdmitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/19 21:43
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Admit")
@BaseResponse
public class AdmitController {
    @Resource
    AdmitService admitService;

    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, String> d){
        return admitService.query(d);
    }
}
