package cn.wecuit.backen.controller;

import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.response.ResponseResult;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.AdmitService;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import org.apache.hc.core5.http.ParseException;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
