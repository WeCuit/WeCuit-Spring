package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.response.ResponseCode;
import cn.wecuit.backen.utils.HTTP.HttpRequestConfig;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HTTP.HttpUtilEntity;
import cn.wecuit.backen.utils.TheolUtil;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/9 6:04
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Theol")
public class TheolController {
    @Resource
    HttpServletResponse response;
    @Value("${wecuit.data-path}")
    String DATA_PATH;

    @PostMapping("/courseList")
    public Map<String, Object> courseListAction(@RequestBody Map<String, String> body) throws IOException, ParseException {
        String cookie = body.get("cookie");

        HttpUtil2 httpUtil2 = new HttpUtil2();
        Map<String, String> headers = new HashMap<String, String>(){{
            put("cookie", cookie);
            put("referer", "http://jxpt.cuit.edu.cn");
        }};
        HttpUtilEntity httpUtilEntity = httpUtil2.doGetEntity("http://jxpt.cuit.edu.cn/meol/lesson/blen.student.lesson.list.jsp", headers, "GBK");
        Map<String, String> cookies = httpUtilEntity.getCookies();
        StringBuilder c = new StringBuilder();
        cookies.forEach((k,v)-> c.append(k).append("=").append(v).append(";"));

        Map<String, Object> ret = new HashMap<>();
        if(cookies.size()!=0) {
            ret.put("code", 21401);
            ret.put("theolCookie", c.toString());
        }else if(httpUtilEntity.getBody().contains("Permission Denied")){
            throw new BaseException(ResponseCode.Theol_NOT_LOGIN);
        }else{
            ret.put("code", 200);
            ret.put("list", TheolUtil.courseListHandle(httpUtilEntity.getBody()));
        }
        return ret;
    }

    @PostMapping("/login")
    public Map<String, Object> loginAction(@RequestBody Map<String, String> body) throws IOException, ParseException {
        StringBuilder sso_tgc = new StringBuilder("TGC=").append(body.get("SSO_TGC")).append(";");
        String theolCookie = body.get("theolCookie");

        Map<String, String> headers = new HashMap<String, String>(){{
            put("cookie", sso_tgc.append(theolCookie).toString());
            put("referer", "http://jxpt.cuit.edu.cn/");
        }};

        HttpUtilEntity httpUtilEntity = new HttpUtil2(new HttpRequestConfig() {{ setMaxRedirects(2);}}).doGetEntity("https://sso.cuit.edu.cn/authserver/login?service=http://jxpt.cuit.edu.cn/meol/homepage/common/sso_login.jsp;" + theolCookie, headers, "GBK");
        int statusCode = httpUtilEntity.getStatusCode();

        Map<String, Object> ret = new HashMap<>();
        if(statusCode == 302){
            ret.put("code", 200);
        }else{
            ret.put("code", 12401);
            ret.put("msg", "SSO未登录");
        }
        return ret;
    }

    @GetMapping("/dirTree")
    public Map<String, Object> dirTreeAction(@RequestParam String lid) throws IOException, ParseException {
        String url ="http://jxpt.cuit.edu.cn/meol/common/script/xmltree.jsp?lid=" + lid + "&groupid=4&_=1716";
        // 免权限，可直接获取
        String body1 = HttpUtil.doGet(url);

        Map<String, Object> map = TheolUtil.dirTreeHandle(body1);

        return new HashMap<String, Object>(){{
            put("code", 200);
            put("dir", map);
        }};

    }

    @PostMapping("/folderList")
    public Map<String, Object> folderListAction(@RequestBody Map<String, String> body) throws IOException, ParseException {
        String lid = body.get("lid");
        String folderId = body.get("folderId");
        String theolCookie = body.get("theolCookie");

        String url = "http://jxpt.cuit.edu.cn/meol/common/script/listview.jsp?lid=" + lid + "&folderid=" + folderId;
        String html = new HttpUtil2().doGet2(url, new HashMap<String, String>() {{
            put("cookie", theolCookie);
        }});
        if(!html.contains("课程资源"))throw new BaseException(403, "获取内容失败");

        Map<String, Object> map = TheolUtil.folderListHandle(html);

        return new HashMap<String, Object>(){{
            put("code", 200);
            put("dir", map);
        }};

    }

    @GetMapping("/downloadFile/1.*")
    public void downloadFileAction(@RequestParam String fileId,
                                   @RequestParam String resId,
                                   @RequestParam String lid,
                                   @RequestParam String cookie) throws IOException, ParseException {
        String fileAddr = DATA_PATH + "/files/theol_" + fileId + "_" + resId + "_" +lid;

        // GET ContentType
        String attr_link = "http://jxpt.cuit.edu.cn/meol/common/script/attribute_file.jsp?lid=" + lid + "&resid=" + resId;
        HttpUtil2 http = new HttpUtil2();
        String attrInfo = http.doGet2(attr_link, new HashMap<String, String>() {{
            put("cookie", cookie);
        }});
        String fileType = TheolUtil.getFileType(attrInfo);
        response.setContentType(fileType);

        String url = "http://jxpt.cuit.edu.cn/meol/common/script/download.jsp?fileid=" + fileId + "&resid=" + resId + "&lid=" +lid;

        ServletOutputStream outputStream = response.getOutputStream();
        byte[] chunk = new byte[4096];

        File file = new File(fileAddr);
        if(file.exists()){

            FileInputStream inputStream = new FileInputStream(file);
            // Path path = Paths.get(fileAddr);
            // String contentType = Files.probeContentType(path);
            response.addHeader("Content-Length", Long.toString(file.length()));
            // response.addHeader("Content-Type", contentType);

            int len;
            while (-1 != (len = inputStream.read(chunk))){
                outputStream.write(chunk, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return ;
        }
        new File(file.getParent()).mkdirs();

        try(CloseableHttpResponse closeableHttpResponse = HttpUtil2.doGet(url, null, new HashMap<String, String>() {{
            put("cookie", cookie);
        }}, null, null)){
            if(closeableHttpResponse == null || 200 != closeableHttpResponse.getCode())throw new BaseException(403, "ERROR");
            HttpEntity entity = closeableHttpResponse.getEntity();

            response.setContentLengthLong(entity.getContentLength());

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            InputStream inputStream = entity.getContent();
            int len;
            while (-1 != (len = inputStream.read(chunk))){
                outputStream.write(chunk, 0, len);
                fileOutputStream.write(chunk, 0, len);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            outputStream.close();

        }

    }

}
