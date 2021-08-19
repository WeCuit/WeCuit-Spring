package cn.wecuit.backen.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class NewsUtil {

    /**
     * 获取每个学院最新的新闻
     * @param listPath 新闻列表存储目录
     * @param dayRange  最近 [dayRange] 天的新闻
     * @return
     * @throws IOException
     */
    public static List<Map<String, String>> getLatestNews(String listPath, int dayRange) throws IOException {
        File listDir = new File(listPath);
        String[] list = listDir.list();

        List<Map<String, String>> latestNews = new LinkedList<>();

        if(list == null){
            return latestNews;
        }

        for (String dir: list){
            // 学院目录
            String xyPath = listPath + "/" + dir;
            File xy = new File(xyPath);
            File[] files = xy.listFiles((dir1, name) -> name.endsWith("_1.json"));

            for(File file: files){
                // 读取内容
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                StringBuilder content = new StringBuilder();
                String line;
                while((line=bufferedReader.readLine()) != null){
                    content.append(line);
                }
                bufferedReader.close();

                // 文件内容读取完毕，开始解析并添加至列表
                List<Map<String, String>> latestList = getLatestItem(content.toString(), dayRange, dir);
                if(null != latestList)
                    latestNews.addAll(latestList);
            }
        }

        latestNews.sort((o1, o2) -> {
            if(!o2.get("date").equals(o1.get("date")))
                return o2.get("date").compareTo(o1.get("date"));
            return o2.get("link").compareTo(o1.get("link"));
        });
        return latestNews;
    }

    private static List<Map<String, String>> getLatestItem(String json, int dayRange, String source){

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd");
        // String today = dateFormat1.format(new Date());
        // dateFormat.parse("").getTime();
        long nowTime = LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));

        Map<String, Object> news = JsonUtil.string2Obj(json, Map.class);
        List<Map<String, String>> list = (List<Map<String, String>>)news.get("list");

        // 获取 {dayRange} 天之内的新闻
        Stream<Map<String, String>> mapStream = list.stream().filter(m -> {
            try {
                String date = m.get("date");
                Long newsTime = date.length() > 5 ? dateFormat1.parse(date).getTime()/1000 : dateFormat2.parse(date).getTime()/1000;
                if(nowTime - newsTime <= 3600L * 24 * dayRange)
                {
                    log.debug("现在时间：{} 新闻时间：{}", nowTime, newsTime);
                    m.put("name", (String)news.get("name"));
                    m.put("domain", (String)news.get("domain"));
                    m.put("source", source);
                    return true;
                }
                return false;
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            return false;
        });
        List<Map<String, String>> collect = mapStream.collect(Collectors.toList());

        return collect;
    }
}
