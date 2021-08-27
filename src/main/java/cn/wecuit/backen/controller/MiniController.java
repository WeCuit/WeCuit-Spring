package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.Option;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.response.ResponseResult;
import cn.wecuit.backen.services.OptionService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author jiyec
 * @Date 2021/8/25 9:45
 * @Version 1.0
 **/
@Api(value = "小程序配置")
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@RequestMapping("/mini")
@BaseResponse
public class MiniController {
    @Resource
    OptionService optionService;

    @GetMapping("/index-slide")
    public ResponseResult indexGet(){
        Object valueByName = optionService.getValueByName("mini_index");
        return new ResponseResult(){{
            setCode(200);
            setData(valueByName);
        }};
    }

    @PatchMapping("/index-slide")
    public ResponseResult indexUpdate(@RequestBody Option option){
        option.setName("mini_index");
        boolean ac = optionService.updateValueByName(option);
        if(!ac){
            ac = optionService.addNew(option);
        }
        boolean finalAc = ac;
        return new ResponseResult(){{
            setCode(finalAc ?200:201);
            setMsg(finalAc?"success":"fail");
        }};
    }
}
