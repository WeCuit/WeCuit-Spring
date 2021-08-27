package cn.wecuit.backen.controller;

import cn.wecuit.backen.response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author jiyec
 * @Date 2021/8/27 16:53
 * @Version 1.0
 **/
@RestController
@RequestMapping("/menu")
public class MenuController {
    @GetMapping("/list")
    public ResponseResult list(){
        return null;
    }
}
