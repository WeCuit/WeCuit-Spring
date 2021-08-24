package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.ResponseData;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.FileService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author jiyec
 * @Date 2021/8/24 7:22
 * @Version 1.0
 **/
@Api("媒体控制器")
@ApiOperation(value="媒体", notes="媒体处理")
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@RequestMapping("/media")
public class MediaController {

    @Value("${wecuit.data-path}/upload")
    private String BASE_UPLOAD_PATH;
    @Resource
    FileService fileService;

    @PutMapping("/upload")
    public ResponseData upload(@RequestPart MultipartFile file, @RequestParam String id){

        // TODO:文件类型限制

        if(file.isEmpty()){
            throw new BaseException(404, "文件上传失败!");
        }
        String suffix = StringUtils.substringAfter(file.getOriginalFilename(), ".");
        Date now = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat sdf2 = new SimpleDateFormat("/yyyy/MM");
        String filename = sdf1.format(now);
        String fileDir = sdf2.format(now);
        String filePath = BASE_UPLOAD_PATH + fileDir + "/" + filename + "." + suffix;

        try {
            File storeFile = new File(filePath);
            storeFile.mkdirs();
            file.transferTo(storeFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(500, "文件存储失败！");
        }

        return new ResponseData(){{
            setCode(200);
            setMsg("success");
        }};
    }

    @GetMapping("/list")
    public ResponseData list(@RequestParam(required = false,defaultValue = "0") int start,
                             @RequestParam(required = false, defaultValue = "9") int end){
        // File path = new File(BASE_UPLOAD_PATH);

        if(start < 0)start = 0;
        if(end < 0) end = 10;

        Map<String, Object> ret = fileService.scanAllFile(BASE_UPLOAD_PATH, start, end);
        return new ResponseData(){{
            setCode(200);
            setMsg("success");
            setData(ret);
        }};
    }

}
