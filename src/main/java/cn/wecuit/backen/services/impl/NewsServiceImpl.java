package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.entity.News;
import cn.wecuit.backen.services.NewsService;
import cn.wecuit.backen.utils.*;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.data.NewsStorage;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/8/18 9:47
 * @Version 1.0
 **/
@Service
@Slf4j
public class NewsServiceImpl implements NewsService {

    @Value("${wecuit.data-path}")
    String BASE_DATA_PATH;
    @Autowired
    ResourceLoader resourceLoader;

    public void pullNews(String dir, News news) {
        new NewsTask(dir, news).run();
    }

    public void pullNews() {

        Resource resource = resourceLoader.getResource("classpath:newsConfig.json");
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String newsConfig = FileUtil.ReadFile(inputStream);

        News[] newsArray = JsonUtil.string2Obj(newsConfig, News[].class);

        // 获取数据缓存路径
        String cachePath = BASE_DATA_PATH + "/WeCuit/cache";

        // 启动多线程处理新闻
        for (News news : newsArray) {
            new NewsTask(cachePath + "/news/list", news).start();
        }
    }

    public void newsNotice(List<String> noticeList) throws IOException {
        // 获取数据缓存路径
        String cachePath = BASE_DATA_PATH + "/WeCuit/cache";
        // 获取最新新闻列表
        List<Map<String, String>> latestNews = NewsUtil.getLatestNews(cachePath + "/news/list", 1);
        latestNews.forEach(news -> {
            //String link = news.get("link");
            String title = news.get("title");
            try {
                byte[] bytes = RSAUtils.genMD5(title.getBytes(StandardCharsets.UTF_8));
                String md5 = HexUtil.byte2HexStr(bytes);

                if (!NewsStorage.isNewsExist(md5)) {
                    // 新的新闻
                    NewsStorage.addNews(md5);

                    noticeList.forEach((id) -> {
                        Group group = RobotMain.getBot().getGroup(Long.parseLong(id));
                        if (group != null) {
                            try {
                                String path = "pages/articleView/articleView";
                                String finalPath = path + "?path=" + news.get("link")
                                        + "&source=" + news.get("source")
                                        + "&domain=" + news.get("domain");
                                String encode = URLEncoder.encode(finalPath, "UTF-8");
                                String url = "https://m.q.qq.com/a/p/1111006861?s=" + encode;
                                group.sendMessage(news.get("title") + "\n"+url);
                                //group.sendMessage(new LightApp(NewsProvider.genLightJson(news)));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else
                            log.info("似乎没有找到群？");
                    });
                } else {
                    log.info("该新闻已提醒");
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
    }

}

@Slf4j
class NewsTask extends Thread {

    private final News news;
    private final String dir;

    /**
     * 构造方法
     *
     * @param dir  存储路径
     * @param news 新闻实体
     */
    public NewsTask(String dir, News news) {
        this.news = news;
        this.dir = dir;
    }

    public void run() {
        String pullFun = "v" + news.getPullVer() + "_pull";

        try {
            // 反射调用指定版本的解析方法
            Method method = this.getClass().getMethod(pullFun);
            method.invoke(this);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void v1_pull() {
        String path = this.dir + "/" + news.getSource();
        File folder = new File(path);

        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }

        news.getTags().forEach(o -> {
            String name = o.get("name");

            int page = 1;

            Map<String, Object> ret;
            boolean next;
            do {
                ret = v1_list(name, page);
                next = (boolean) ret.get("next");
                ret.remove("next");
                ret.put("name", news.getName());
                FileUtil.WriteFile(path + "/" + name + "_" + page + ".json", JsonUtil.obj2String(ret));
                page++;
            } while (next);

            o.put("total", Integer.toString(page));
        });
        FileUtil.WriteFile(path + "/tags.json", JsonUtil.obj2String(news.getTags()));
    }

    private Map<String, Object> v1_list(String tag, int page) {
        String uri = news.getUriExp().replace("#tag#", tag);
        uri = uri.replace("#page#", page + "");
        Map<String, Object> ret = new HashMap<>();

        try {
            ret.put("domain", new URL(uri).getHost());
            ret.put("next", false);
            List<Map<String, String>> list = new LinkedList<>();
            ret.put("list", list);

            String body = HttpUtil.doGet(uri);

            News.PatternType pattern = news.getPattern();
            News.PatternType.PatternPos pos = pattern.getPos();
            Pattern compile = Pattern.compile(pattern.getRule());
            Matcher matcher = compile.matcher(body);

            ret.put("next", body.contains("class=\"Next\">下页</a>"));

            Map<String, String> jo;
            while (matcher.find()) {
                jo = new HashMap<>();
                jo.put("date", matcher.group(pos.getDate()).replaceAll("/", "-").replaceAll("\\[|]", ""));
                jo.put("title", matcher.group(pos.getTitle()));
                jo.put("link", matcher.group(pos.getLink()));
                if (!matcher.group(2).contains("党"))
                    list.add(jo);
            }

            if (news.isSort())
                list.sort((o1, o2) -> {
                    if (!o2.get("date").equals(o1.get("date")))
                        return o2.get("date").compareTo(o1.get("date"));
                    return o2.get("link").compareTo(o1.get("link"));
                });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    // 版本二
    public void v2_pull() {
        String path = this.dir + "/" + news.getSource();
        File folder = new File(path);

        if (!folder.exists() && !folder.isDirectory()) {
            System.out.println(folder.mkdirs());
        }

        news.getTags().forEach(o -> {
            String name = o.get("name");

            int i = 1;
            Map<String, Object> ret;
            String page = null;
            do {
                ret = v2_list(name, page);
                page = (String) ret.get("next");
                ret.remove("next");
                ret.put("name", news.getName());
                FileUtil.WriteFile(path + "/" + name + "_" + i + ".json", JsonUtil.obj2String(ret));
                i++;
            } while (null != page);

            o.put("total", Integer.toString(i));
        });
        FileUtil.WriteFile(path + "/tags.json", JsonUtil.obj2String(news.getTags()));
    }

    private Map<String, Object> v2_list(String tag, String page) {
        String uri = news.getUriExp().replace("#tag#", tag);
        uri += (null != page ? "/" + page : ".htm");
        String link_pre = uri.replaceFirst("\\w+\\.htm", "");

        Map<String, Object> ret = new HashMap<>();

        try {
            ret.put("domain", new URL(uri).getHost());
            ret.put("next", false);
            List<Map<String, String>> list = new LinkedList<>();
            ret.put("list", list);

            String body = HttpUtil.doGet(uri);

            // 处理是否有下一页
            Pattern compile = Pattern.compile("(\\d+\\.htm)\"[^<]+下页");
            Matcher matcher = compile.matcher(body);
            if (matcher.find()) {
                String next = matcher.group(1);
                ret.put("next", next);
            } else {
                ret.put("next", null);
            }
            News.PatternType pattern = news.getPattern();
            News.PatternType.PatternPos pos = pattern.getPos();
            // 解析列表
            compile = Pattern.compile(pattern.getRule());
            matcher = compile.matcher(body);

            Map<String, String> jo;
            while (matcher.find()) {
                // 真实路径处理
                String link = link_pre + matcher.group(pos.getLink());
                URL url = new URL(link);
                link = url.getPath() + (url.getQuery() != null ? "?" + url.getQuery() : "");
                link = getRealPath(link);

                jo = new HashMap<>();
                jo.put("date", matcher.group(pos.getDate()).replaceAll("/", "-").replaceAll("\\[|]", ""));
                jo.put("title", matcher.group(pos.getTitle()));
                jo.put("link", link);
                if (!matcher.group(2).contains("党"))
                    list.add(jo);
            }

            if (news.isSort())
                list.sort((o1, o2) -> {
                    if (!o2.get("date").equals(o1.get("date")))
                        return o2.get("date").compareTo(o1.get("date"));
                    return o2.get("link").compareTo(o1.get("link"));
                });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    // 版本四
    public void v4_pull() {
        String path = this.dir + "/" + news.getSource();
        File folder = new File(path);

        if (!folder.exists() && !folder.isDirectory()) {
            System.out.println(folder.mkdirs());
        }

        news.getTags().forEach(o -> {
            String name = o.get("name");

            int i = 1;
            Map<String, Object> ret;
            String page = null;
            int pageCnt = 0;
            do {
                ret = v4_list(name, page);
                page = (String) ret.get("next");
                ret.remove("next");
                ret.put("name", news.getName());
                FileUtil.WriteFile(path + "/" + name + "_" + i + ".json", JsonUtil.obj2String(ret));
                i++;
            } while (null != page && ++pageCnt < 5);


            o.put("total", Integer.toString(i));
        });
        FileUtil.WriteFile(path + "/tags.json", JsonUtil.obj2String(news.getTags()));
    }

    private Map<String, Object> v4_list(String tag, String page) {
        String uri = news.getUriExp().replace("#tag#", tag);
        uri += (null != page ? "/" + page : ".htm");
        String link_pre = uri.replaceFirst("\\w+\\.htm", "");

        Map<String, Object> ret = new HashMap<>();

        try {
            ret.put("domain", new URL(uri).getHost());
            ret.put("next", false);
            List<Map<String, String>> list = new LinkedList<>();
            ret.put("list", list);

            String body = HttpUtil.doGet(uri);

            // 处理是否有下一页
            Pattern compile = Pattern.compile("(\\d+\\.htm)\"[^<]+下页");
            Matcher matcher = compile.matcher(body);
            if (matcher.find()) {
                String next = matcher.group(1);
                ret.put("next", next);
            } else {
                ret.put("next", null);
            }

            News.PatternType pattern = news.getPattern();
            News.PatternType.PatternPos pos = pattern.getPos();
            // 解析列表
            compile = Pattern.compile(pattern.getRule());
            matcher = compile.matcher(body);

            Map<String, String> jo;
            while (matcher.find()) {
                // 真实路径处理
                String link = link_pre + matcher.group(pos.getLink());
                link = new URL(link).getPath();
                link = getRealPath(link);

                jo = new HashMap<>();
                jo.put("date", matcher.group(pos.getDate()).replaceAll("/", "-").replaceAll("\\[|]", ""));
                jo.put("title", matcher.group(pos.getTitle()));
                jo.put("link", link);
                if (!matcher.group(3).contains("党"))
                    list.add(jo);
            }

            if (news.isSort())
                list.sort((o1, o2) -> {
                    if (!o2.get("date").equals(o1.get("date")))
                        return o2.get("date").compareTo(o1.get("date"));
                    return o2.get("link").compareTo(o1.get("link"));
                });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String getRealPath(String filename) {
        String split = "/";
        while (filename.contains(split + '.')) {
            filename = filename.replaceAll("/\\w+/\\.\\./", "/");
            filename = filename.replaceAll("/\\./", "/");
        }
        return filename;
    }

    // // 版本三
    // @Deprecated
    // public void v3_pull() {
    //     String path = this.dir + "/" + news.getSource();
    //     File folder = new File(path);
    //
    //     if (!folder.exists() && !folder.isDirectory()) {
    //         System.out.println(folder.mkdirs());
    //     }
    //
    //     news.getTags().forEach(o -> {
    //         String name = o.get("name");
    //         try {
    //             Map<String, Object> v3_list = v3_list(name);
    //             v3_list.put("name", news.getName());
    //             FileUtil.WriteFile(path + "/" + name + "_1.json", JsonUtil.obj2String(v3_list));
    //         } catch (IOException | ParseException e) {
    //             e.printStackTrace();
    //         }
    //         o.put("total", "1");
    //     });
    //
    //     FileUtil.WriteFile(path + "/tags.json", JsonUtil.obj2String(news.getTags()));
    // }
    // @Deprecated
    // private Map<String, Object> v3_list(String tag) throws IOException, ParseException {
    //     String html = HttpUtil.doGet("https://www.cuit.edu.cn/NewsList?id=" + tag);
    //     html = html.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
    //
    //     List<Map<String, String>> list = new LinkedList<>();
    //     Map<String, Object> ret = new HashMap<String, Object>() {{
    //         put("domain", "www.cuit.edu.cn");
    //         put("list", list);
    //     }};
    //
    //     JXDocument jxDocument = JXDocument.create(html);
    //     List<JXNode> jxNodes = jxDocument.selN("//*[@id=\"NewsListContent\"]/li");
    //     jxNodes.forEach(e -> {
    //         Element element = e.asElement();
    //         String title = element.child(1).text();
    //         String link = element.child(1).attr("href");
    //         String date = element.child(2).text().replaceAll("/", "-").replaceAll("\\[|]", "");
    //
    //         if (!title.contains("党"))
    //             list.add(new HashMap<String, String>() {{
    //                 put("title", title);
    //                 put("link", link);
    //                 put("date", date);
    //             }});
    //     });
    //
    //     return ret;
    // }

}
