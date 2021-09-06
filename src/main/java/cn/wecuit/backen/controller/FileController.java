package cn.wecuit.backen.controller;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.URLUtil;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/5 20:02
 * @Version 1.0
 **/
@RestController
@RequestMapping("/File")
public class FileController {
    @Resource
    HttpServletRequest request;
    @Resource
    HttpServletResponse response;

    @RequestMapping("/transferV2")
    public void transferV2() throws IOException {
        String link = request.getParameter("link");
        String page = request.getParameter("page");
        ServletContext servletContext = request.getServletContext();
        // 缓冲块大小
        final int chunkSize = 1024 * 5;

        // 编码文件名
        String filename = new URL(link).getFile();
        System.out.println(filename);

        // 文件缓存地址
        String fileLocal = servletContext.getInitParameter("CACHE_PATH") + filename;

        // 取前缀 [部分学院文件存放为相对路径]
        String link_pre = page.replaceFirst("\\w+\\.htm", "");

        if(link.startsWith("/")){
            // 绝对路径
            link = "http://" + new URL(link_pre).getHost() + link;
        }else if(!link.contains("http")){
            // 相对路径
            link = link_pre + link;
            link = URLUtil.getRealPath(link);
        }

        // 处理后格式也不正确
        if(!link.startsWith("http"))throw new BaseException(1, "链接格式错误");

        // 输出至用户的 输出流
        ServletOutputStream outputStream = response.getOutputStream();
        java.io.File file = new java.io.File(fileLocal);
        if(file.exists() && file.length() > 0){
            Path path = Paths.get(fileLocal);
            String contentType = Files.probeContentType(path);
            response.addHeader("Content-Length", Long.toString(file.length()));
            response.addHeader("Content-Type", contentType);

            InputStream inputStream = new FileInputStream(fileLocal);
            byte[] chunk = new byte[chunkSize];
            int len = -1;
            while(-1 != (len = inputStream.read(chunk))){
                outputStream.write(chunk, 0, len);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            return;
        }

        // 创建文件所在文件夹，如果没有 getParent 将会创建一个与文件同名的文件夹
        new java.io.File(file.getParent()).mkdirs();

        CloseableHttpResponse closeableHttpResponse = HttpUtil.doGet(link, null, null, "UTF-8");
        HttpEntity entity = closeableHttpResponse.getEntity();

        // 下载至服务器的 输入流
        InputStream is = entity.getContent();
        long contentLength = entity.getContentLength();

        // 设置响应头
        response.setContentType(entity.getContentType());
        response.setContentLengthLong(contentLength);

        // 存储至硬盘的 输出流
        OutputStream outputStream1 = new FileOutputStream(fileLocal);

        byte[] chunk = new byte[chunkSize];
        int len = -1;
        while(-1 != (len = is.read(chunk))){
            outputStream.write(chunk, 0, len);
            outputStream1.write(chunk, 0, len);
        }

        // 刷新并关闭硬盘输出流
        outputStream1.flush();
        outputStream1.close();
        // 关闭下载输入流，关闭HTTP链接
        is.close();
        closeableHttpResponse.close();
        // 关闭前端输出流
        outputStream.flush();
        outputStream.close();

    }

    @GetMapping("/redirect")
    public void redirect(@RequestParam String link, HttpServletResponse response) throws IOException {
        response.sendRedirect(link);
    }
}
