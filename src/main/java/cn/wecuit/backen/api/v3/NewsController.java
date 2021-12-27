package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.NewsService;
import cn.wecuit.backen.utils.FileUtil;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.HexUtil;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.StringUtil.AbstractReplaceCallBack;
import cn.wecuit.backen.utils.StringUtil.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.tika.Tika;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/8/5 22:34
 * @Version 1.0
 **/
@BaseResponse
@RestController
@RequestMapping("/News")
@Slf4j
public class NewsController {

    @Value("${wecuit.data-path}")
    String BASE_DATA_PATH;

    @Resource
    HttpServletRequest request;
    @Resource
    NewsService newsService;

    /**
     * 拉取新闻操作 [对普通用户不可见]
     */
    @GetMapping("/doPull")
    //@SaCheckRole("admin")
    public void doPullAction() {
        newsService.pullNews();
    }

    /**
     * 前端根据来源获取标签
     *
     * @throws IOException response.getWriter()
     */
    @GetMapping("/getTagsV2/source/{source}")
    public Map<String, Object>[] getTagsV2Action(@PathVariable String source) throws IOException {
        String cachePath = BASE_DATA_PATH + "/WeCuit/cache";
        String file = cachePath + "/news/list/" + source + "/tags.json";

        String fileContent = FileUtil.ReadFile(file);

        if (fileContent.length() <= 0) {

            newsService.pullNews();
            throw new BaseException(404, "标签数据不存在");
        }

        return JsonUtil.string2Obj(fileContent, Map[].class);
    }

    /**
     * 根据新闻标签与来源 获取新闻列表
     *
     * @throws IOException response.getWriter()
     */
    @GetMapping("/getList/source/{source}/tag/{tag}/page/{page}")
    public Map<String, Object> getListAction(@PathVariable String source, @PathVariable String tag, @PathVariable String page) throws IOException {

        String cachePath = BASE_DATA_PATH + "/WeCuit/cache";
        // 规则处理
        String file = cachePath + "/news/list/" + source + "/" + tag + "_" + page + ".json";

        String list = FileUtil.ReadFile(file);
        if (list.length() <= 0)
            throw new BaseException(20404, "列表数据不存在");
        return JsonUtil.string2Obj(list, Map.class);
    }

    /**
     * 根据新闻链接获取内容
     *
     * @throws IOException              response.getWriter()
     * @throws ParseException           HttpUtil
     * @throws NoSuchAlgorithmException MD5计算
     */
    @GetMapping("/getContent")
    public String getContentAction() throws IOException, ParseException, NoSuchAlgorithmException {
        String link = request.getParameter("link").replaceFirst("http://", "https://");

        byte[] md5s = MessageDigest.getInstance("md5").digest((link).getBytes(StandardCharsets.UTF_8));
        String md5 = HexUtil.byte2HexStr(md5s);
        String cachePath = BASE_DATA_PATH + "/WeCuit/cache";
        String cacheFile = cachePath + "/news/content/" + md5 + ".html";
        File file = new File(cacheFile);

        // 是否需要更新缓存    缓存时间[秒]
        boolean update = System.currentTimeMillis() / 1000 - file.lastModified() / 1000 > 60 * 60 * 30;

        // 缓存文件存在，输出缓存文件
        if (file.exists() && !update) {
            return FileUtil.ReadFile(cacheFile);
        }

        // 缓存不存在
        String html = getNewsContent(link);

        // 写入缓存
        FileUtil.WriteFile(cacheFile, html);
        return html;
    }

    @PostMapping("/downFilePrepare")
    public Map<String, Object> downFile(@RequestBody Map<String, String> body, HttpServletResponse response, HttpServletRequest request) {
        String cookie = body.get("cookie");
        String downUrl = body.get("downUrl");
        String codeValue = body.get("codeValue");
        String domain = body.get("domain");

        HttpUtil2 http = new HttpUtil2();
        String url = "http://" + domain + downUrl + "&codeValue=" + codeValue;
        String finalCookie = cookie;
        Map<String, String> headers = new HashMap<String, String>() {{
            put("cookie", finalCookie);
            put("referer", "https://" + domain);
        }};
        CloseableHttpResponse closeableHttpResponse = http.doGet(url, null, headers, "utf-8");
        try {
            String contentType = closeableHttpResponse.getEntity().getContentType();

            if (contentType.contains("octet-stream")) {
                // 文件可以下载

                String link;

                String fileName = closeableHttpResponse.getHeader("Content-disposition").getValue();
                Pattern compile = Pattern.compile("\\.([a-zA-Z]+);");
                Matcher matcher = compile.matcher(fileName);
                String suffix = "";
                if (matcher.find()) {
                    suffix = matcher.group(1);
                }
                downUrl = URLEncoder.encode(url, "utf-8");
                cookie = URLEncoder.encode(cookie, "utf-8");

                link = "https://" + request.getHeader("HOST") + "/v3/News/downFile/suffix." + suffix + "?url=" + downUrl + "&cookie=" + cookie;

                return new HashMap<String, Object>() {{
                    put("link", link);
                }};
            } else {
                // 验证码
                Map<String, String> cookie1 = http.getCookie();
                if (!cookie1.isEmpty()) {
                    cookie = "JSESSIONID=" + cookie1.get("JSESSIONID");
                    headers.clear();
                    headers.put("cookie", cookie);
                }
                String captchaUrl = "https://" + domain + "/system/resource/js/filedownload/createimage.jsp?randnum=" + new Date().getTime();

                // 请求验证码，写入bos
                CloseableHttpResponse captchaReq = http.doGet(captchaUrl, null, headers, null);
                InputStream content = captchaReq.getEntity().getContent();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] chunk = new byte[1024];
                int read = content.read(chunk);
                while (read != -1) {
                    bos.write(chunk, 0, read);
                    read = content.read(chunk);
                }
                content.close();
                captchaReq.close();

                final byte[] captcha = bos.toByteArray();
                bos.close();

                return new HashMap<String, Object>() {{
                    put("captcha", Base64.getEncoder().encodeToString(captcha));
                    put("cookie", headers.get("cookie"));
                }};
            }
        } catch (IOException | ProtocolException e) {
            e.printStackTrace();
            throw new BaseException(500, "网络");
        }
    }

    @GetMapping("/downFile/suffix.{suffix}")
    public void downFile(@RequestParam String url, @RequestParam String cookie, @PathVariable String suffix, HttpServletResponse response) {

        HttpUtil2 http = new HttpUtil2();
        Map<String, String> headers = new HashMap<String, String>() {{
            put("cookie", cookie);
            put("referer", url);
        }};
        CloseableHttpResponse closeableHttpResponse = http.doGet(url, null, headers, "utf-8");
        InputStream content;
        try {
            Header[] headers1 = closeableHttpResponse.getHeaders();
            for (Header header : headers1) {
                response.setHeader(header.getName(), header.getValue());
            }

            // 取mime
            String type = new Tika().detect("." + suffix);
            response.setHeader("Content-Type", type);

            content = closeableHttpResponse.getEntity().getContent();
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] chunk = new byte[1024 * 100];
            int read = content.read(chunk);
            while (-1 != read) {
                outputStream.write(chunk, 0, read);
                read = content.read(chunk);
            }
            content.close();
            closeableHttpResponse.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 其它新闻解析
     *
     * @param link 新闻链接
     * @return String 截取后的新闻内容
     */
    private String getNewsContent(String link) throws IOException, ParseException {
        link = link.replace("n//", "n/");
        String html = HttpUtil.doGet(link);
        JXDocument jxDocument = JXDocument.create(html);

        JXNode jxNode = jxDocument.selNOne("//body/title");
        if (jxNode == null)
            jxNode = jxDocument.selNOne("//head/title");

        String title = jxNode == null ? "标题失踪了" : jxNode.asString();
        URL url = new URL(link);
        String host = url.getHost();

        Map<String, String> xpathMap = new HashMap<String, String>() {{
            put("glxy.cuit.edu.cn", "//body/table[2]/tbody/tr/td/table[2]/tbody/tr[2]/td[4]/table/tbody/tr[2]/td/form/table");
            put("tjx.cuit.edu.cn", "//body/table[3]/tbody/tr[1]/td[3]/table[2]/tbody/tr/td/form/table");
            put("whys.cuit.edu.cn", "//body/table[2]/tbody/tr/td/table[2]/tbody/tr[2]/td[4]/table/tbody/tr[2]/td/form/table");
            put("wlxy.cuit.edu.cn", "//body/table[5]/tbody/tr[2]/td[4]/table/tbody/tr[2]/td/div/form/table");
            put("cas.cuit.edu.cn", "//body/table[5]/tbody/tr/td[1]/table[3]/tbody/tr/td[2]/table/tbody/tr/td/form/table");
            put("gdgcxy.cuit.edu.cn", "//body/div[5]/div[2]/form/div");
            put("jsjxy.cuit.edu.cn", "//body/div[2]/div[2]/div/div/div/form/div/div/div/div");
            put("kzgcxy.cuit.edu.cn", "//*[@id=\"vsb_newscontent\"]");
            put("rjgcxy.cuit.edu.cn", "//body/table[4]/tbody/tr/td[2]/table[2]/tbody/tr/td/table/tbody/tr[2]/td/form/table");
            put("txgcxy.cuit.edu.cn", "//body/div[4]/div[2]/div/div[2]/div/form/table");
            put("wgyxy.cuit.edu.cn", "//*[@id=\"vsb_content\"]");
            put("cyber.cuit.edu.cn", "//body/div[3]/div[2]/div[2]/form/div");
            put("math.cuit.edu.cn", "//body/div[4]/div/div[2]/div/div/div/div/table/tbody/tr/td");
            put("hjgcx.cuit.edu.cn", "//body/div[4]/div/div[2]/ul/table/tbody/tr[2]/td/form/table/tbody");
            put("qkl.cuit.edu.cn", "//body/div[4]/div/div[2]/div[2]/form/div");
            put("jwc.cuit.edu.cn", "//body/nav[3]/form/div");
            put("dzgcxy.cuit.edu.cn", "//body/table/tbody/tr[4]/td/table/tbody/tr/td[4]/table/tbody/tr[3]/td/table/tbody/tr/td/form/table");
            put("www.cuit.edu.cn", "//body/div[3]/div/div[2]/div/form/div");
        }};
        String xpath = xpathMap.get(host);

        if (null == xpath) throw new BaseException(20400, "不支持的来源" + link);

        jxNode = jxDocument.selNOne(xpath);
        if (null == jxNode) throw new BaseException(10500, "解析失败");

        String body = jxNode.asString();

        // ”回调“替换处理
        body = StringUtils.replaceAll(body, "<img[\\s\\S]*?src=\"(.*?)\"", new AbstractReplaceCallBack() {
            @Override
            public String doReplace(String text, int index, Matcher matcher) {
                if (!$(1).startsWith("http"))
                    return text.replace("src=\"", "src=\"https://jwc.cuit.edu.cn/");
                return text;
            }
        });

        // ”关闭窗口“ 字符处理
        body = body.replaceAll("<span>.*?<span>关闭.*?</span>.*?</span>", "");
        return title + body;
    }

    /**
     * 主站新闻处理
     * 主站新闻在详情页又使用iframe包裹了一层，需单独处理
     *
     * @param link 主站新闻链接
     * @return String 新闻主体html
     * @throws IOException 流异常 [来自HTTP请求处理]
     */
    @Deprecated
    private String getHomeContent(String link) throws IOException, ParseException {
        link = link.replace("http://", "https://").replace(".aspx", "");
        String html = HttpUtil.doGet(link);

        JXDocument jxDocument = JXDocument.create(html);
        JXNode jxNode = jxDocument.selNOne("//*[@id=\"NewsContent\"]");
        if (null == jxNode) throw new BaseException(20500, "内容解析失败");
        String src = jxNode.asElement().attr("src");

        html = HttpUtil.doGet("https://www.cuit.edu.cn" + src);

        jxDocument = JXDocument.create(html);
        String title = jxDocument.selNOne("//head/title").asString();
        String body = jxDocument.selNOne("//body").asString().replace("href=\"/News/file/", "href=\"https://www.cuit.edu.cn/News/file/");

        return title + body;
    }


}
