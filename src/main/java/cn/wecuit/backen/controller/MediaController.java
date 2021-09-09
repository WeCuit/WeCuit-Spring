package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.Media;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.response.ResponseResult;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.FileService;
import cn.wecuit.backen.services.MediaService;
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
@BaseResponse
public class MediaController {

    @Value("${wecuit.data-path}")
    private String BASE_STORE_PATH;
    @Resource
    FileService fileService;
    @Resource
    MediaService mediaService;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestPart MultipartFile file){

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
        String storePath = "/upload" + fileDir + "/" + filename + "." + suffix;
        String filePath = BASE_STORE_PATH + storePath;

        try {
            File storeFile = new File(filePath);
            storeFile.mkdirs();
            file.transferTo(storeFile);
            boolean store = mediaService.store(new Media() {{
                setPath(storePath);
            }});
            if(!store){
                storeFile.delete();
                throw new BaseException(500, "文件数据存储失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(500, "文件存储失败！");
        }

        return new HashMap<String,Object>(){{
            put("result", true);
        }};
    }

    @GetMapping("/localList")
    public Map<String, Object> localList(@RequestParam(required = false,defaultValue = "0") int start,
                                    @RequestParam(required = false, defaultValue = "9") int end){
        // File path = new File(BASE_UPLOAD_PATH);

        if(start < 0)start = 0;
        if(end < 0) end = 10;

        return fileService.scanAllFile(BASE_STORE_PATH, start, end);
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false, defaultValue = "1") int page,
                               @RequestParam(required = false, defaultValue = "10") int limit){
        if(page <= 0)page = 1;
        return mediaService.list(page, limit);
    }

    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam long id){
        boolean delete = mediaService.delete(id);
        return new HashMap<String, Object>(){{
            put("result", delete);
        }};
    }
}
