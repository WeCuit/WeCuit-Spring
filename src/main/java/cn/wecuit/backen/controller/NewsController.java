package cn.wecuit.backen.controller;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.NewsService;
import cn.wecuit.backen.utils.FileUtil;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.HexUtil;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.NewsUtil;
import cn.wecuit.backen.utils.StringUtil.AbstractReplaceCallBack;
import cn.wecuit.backen.utils.StringUtil.StringUtils;
import org.apache.hc.core5.http.ParseException;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @Author jiyec
 * @Date 2021/8/5 22:34
 * @Version 1.0
 **/
@RestController
@RequestMapping("/News")
public class NewsController {

    @Value("${wecuit.data-path}")
    String BASE_DATA_PATH;
    
    @Resource
    HttpServletRequest request;
    @Resource
    NewsService newsService;

    /**
     * 拉取新闻操作 [对普通用户不可见]
     *
     */
    @GetMapping("/doPull")
    public void doPullAction(){
        newsService.pullNews();
    }

    /**
     * 前端根据来源获取标签
     *
     * @throws IOException response.getWriter()
     */
    @GetMapping("/getTagsV2/source/{source}")
    public Map<String,Object> getTagsV2Action(@PathVariable String source) throws IOException {
        String cachePath = BASE_DATA_PATH + "/WeCuit/cache";
        String file = cachePath + "/news/list/" + source + "/tags.json";

        String fileContent = FileUtil.ReadFile(file);

        if(fileContent.length() <= 0)
            throw new BaseException(404, "标签数据不存在");

        return new HashMap<String, Object>(){{
            put("code", 200);
            put("data", JsonUtil.string2Obj(fileContent, Map[].class));
        }};
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
        if(list.length() <= 0)
            throw new BaseException(20404, "列表数据不存在");
        return new HashMap<String, Object>(){{
            put("code", 200);
            put("data", JsonUtil.string2Obj(list, Map.class));
        }};
    }

    /**
     * 根据新闻链接获取内容
     *
     * @throws IOException response.getWriter()
     * @throws ParseException HttpUtil
     * @throws NoSuchAlgorithmException MD5计算
     */
    @GetMapping("/getContent/source/{source}")
    public String getContentAction(@PathVariable String source) throws IOException, ParseException, NoSuchAlgorithmException {
        String link = request.getParameter("link").replaceFirst("http://", "https://");

        byte[] md5s = MessageDigest.getInstance("md5").digest((source + link).getBytes(StandardCharsets.UTF_8));
        String md5 = HexUtil.byte2HexStr(md5s);
        String cachePath = BASE_DATA_PATH + "/WeCuit/cache";
        String cacheFile = cachePath + "/news/content/" + md5 + ".html";
        File file = new File(cacheFile);

        // 缓存时间[秒]
        boolean update = System.currentTimeMillis()/1000 - file.lastModified()/1000 > 60 * 60 * 30;

        // 缓存文件存在，输出缓存文件
        if(file.exists() && !update){
            return FileUtil.ReadFile(cacheFile);
        }

        // 缓存不存在
        String html = getOtherContent(link, source);


        // 写入缓存
        FileUtil.WriteFile(cacheFile, html);
        return html;
    }

    /**
     * 其它新闻解析
     *
     * @param link 新闻链接
     * @param source    来源
     * @return  String 截取后的新闻内容
     */
    private String getOtherContent(String link, String source) throws IOException, ParseException {
        link = link.replace("n//", "n/");
        String html = HttpUtil.doGet(link);
        JXDocument jxDocument = JXDocument.create(html);

        JXNode jxNode = jxDocument.selNOne("//body/title");

        String title = jxNode == null ? "标题失踪了" : jxNode.asString();

        Map<String, String> xpathMap = new HashMap<String, String>(){{
            put("gl", "//body/table[2]/tbody/tr/td/table[2]/tbody/tr[2]/td[4]/table/tbody/tr[2]/td/form/table");
            put("tj", "//body/table[3]/tbody/tr[1]/td[3]/table[2]/tbody/tr/td/form/table");
            put("whys", "//body/table[2]/tbody/tr/td/table[2]/tbody/tr[2]/td[4]/table/tbody/tr[2]/td/form/table");
            put("wl", "//body/table[5]/tbody/tr[2]/td[4]/table/tbody/tr[2]/td/div/form/table");
            put("dqkx", "//body/table[5]/tbody/tr/td[1]/table[3]/tbody/tr/td[2]/table/tbody/tr/td/form/table");
            put("gdgc", "//body/div[5]/div[2]/form/div");
            put("compute", "//body/div[2]/div[2]/div/div/div/form/div/div/div/div");
            put("kzgc", "//*[@id=\"vsb_content\"]");
            put("rjgc", "//body/table[4]/tbody/tr/td[2]/table[2]/tbody/tr/td/table/tbody/tr[2]/td/form/table");
            put("txgc", "//body/div[2]/div/div[2]/div[2]/form");
            put("wgy", "//*[@id=\"vsb_content\"]");
            put("wlaq", "//body/div[3]/div[2]/div[2]/form/div");
            put("yysx", "//body/div[4]/div/div[2]/div/div/div/div/table/tbody/tr/td");
            put("zyhj", "//body/table[3]/tbody/tr[1]/td[3]/table[2]/tbody/tr/td/form/table");
            put("qkl", "//body/div[4]/div/div[2]/div[2]/form/div");
            put("jwc", "//body/nav[3]/form/div");
            put("dzgc", "//body/table/tbody/tr[4]/td/table/tbody/tr/td[4]/table/tbody/tr[3]/td/table/tbody/tr/td/form/table");
            put("home", "//body/div[3]/div/div[2]/div/form/div");
        }};
        String xpath = xpathMap.get(source);

        if(null == xpath)throw new BaseException(20400, "未知来源");

        jxNode = jxDocument.selNOne(xpath);
        if(null == jxNode)throw new BaseException(10500, "解析失败");

        String body = jxNode.asString();

        // ”回调“替换处理
        body = StringUtils.replaceAll(body, "<img[\\s\\S]*?src=\"(.*?)\"", new AbstractReplaceCallBack(){
            @Override
            public String doReplace(String text, int index, Matcher matcher) {
                if(!$(1).startsWith("http"))
                    return text.replace("src=\"", "src=\"https://jwc.cuit.edu.cn/");
                return text;
            }
        });

        // ”关闭窗口“ 字符处理
        body = body.replaceAll("<span>.*?<span>关闭.*?</span>.*?</span>", "");
        return title + body;
    }

    /**
     *  主站新闻处理
     *  主站新闻在详情页又使用iframe包裹了一层，需单独处理
     *
     * @param link 主站新闻链接
     * @return  String 新闻主体html
     * @throws IOException 流异常 [来自HTTP请求处理]
     */
    private String getHomeContent(String link) throws IOException, ParseException {
        link = link.replace("http://", "https://").replace(".aspx", "");
        String html = HttpUtil.doGet(link);

        JXDocument jxDocument = JXDocument.create(html);
        JXNode jxNode = jxDocument.selNOne("//*[@id=\"NewsContent\"]");
        if(null == jxNode)throw new BaseException(20500, "内容解析失败");
        String src = jxNode.asElement().attr("src");

        html = HttpUtil.doGet("https://www.cuit.edu.cn" + src);

        jxDocument = JXDocument.create(html);
        String title = jxDocument.selNOne("//head/title").asString();
        String body = jxDocument.selNOne("//body").asString().replace("href=\"/News/file/", "href=\"https://www.cuit.edu.cn/News/file/");

        return title+body;
    }
}
