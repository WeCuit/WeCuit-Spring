package cn.wecuit.robot.plugins.msg;

import cn.wecuit.backen.services.NewsService;
import cn.wecuit.backen.utils.SpringUtil;
import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.services.RbNewsService;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@RobotPlugin
@MainCmd(keyword = "新闻系统", desc = "Admin:\n\nUser:\n查询 n -- 查询n天之内的新闻")
public class NewsPlugin extends MsgPluginImpl {

    @Value("${wecuit.data-path}")
    String BASE_DATA_PATH;

    private static final List<String> enabledList = new ArrayList<>();
    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("enabledList", enabledList);
    }};

    @SubCmd(keyword = "开启推送", desc = "开启对应群聊的新闻推送功能", requireAdmin = true)
    public boolean enablePush(GroupMessageEvent event){

        String fromId = Long.toString(event.getSubject().getId());
        boolean newsNotice = enabledList.contains(fromId);
        if(!newsNotice) {
            event.getSubject().sendMessage("已为阁下开启该功能");
            enabledList.add(fromId);
            updatePluginData();
        }else{
            event.getSubject().sendMessage("已经是开启状态啦~");
        }
        return true;
    }

    @SubCmd(keyword = "关闭推送", desc = "关闭对应群聊的新闻推送功能", requireAdmin = true)
    public boolean disablePush(GroupMessageEvent event){

        String fromId = Long.toString(event.getSubject().getId());
        boolean newsNotice = enabledList.contains(fromId);
        if(newsNotice) {
            event.getSubject().sendMessage("已为阁下关闭该功能");
            enabledList.remove(fromId);
            updatePluginData();
        }else{
            event.getSubject().sendMessage("已经是关闭状态啦~");
        }
        return true;
    }

    @SubCmd(keyword = "添加推送目标", desc = "参数[群号]，添加指定群号到推送列表", requireAdmin = true)
    public boolean addPushTarget(GroupMessageEvent event, CmdList cmds){

        if(cmds.size() != 1){
            event.getSubject().sendMessage("阁下的指令格式好像不太对呢(・∀・(・∀・(・∀・*)");
            return true;
        }
        String targetId = cmds.get(0);

        boolean newsNotice = enabledList.contains(targetId);
        if(!newsNotice){
            event.getSubject().sendMessage("已为阁下添加目标：" + targetId);
            enabledList.add(targetId);
            updatePluginData();
        }
        return true;
    }

    // 推送测试
    @SubCmd(keyword = "推送测试", desc = "在当前群进行一次推送测试", requireAdmin = true)
    public boolean pushTest(GroupMessageEvent event) throws IOException {
        String subjectId = Long.toString(event.getSubject().getId());
        NewsService newsService = SpringUtil.getBean(NewsService.class);
        newsService.newsNotice(new ArrayList<String>(){{add(subjectId);}});
        return true;
    }

    @SubCmd(keyword = "查询", desc = "参数[n]，查询n天之内的新闻")
    public boolean query(GroupMessageEvent event, List<String> cmds){
        try{
            int dayRange = 1;
            if(cmds.size() > 0)
                dayRange = Integer.parseInt(cmds.get(0));

            RbNewsService newsService = SpringUtil.getBean(RbNewsService.class);
            List<Map<String, String>> todayNews = newsService.getLatestNews(dayRange);

            if(todayNews.size() == 0) {
                event.getSubject().sendMessage("没有" + dayRange + "天之内的新闻");
                return true;
            }

            todayNews.forEach(news->{
                try {
                    String path = "pages/articleView/articleView";
                    String finalPath = path + "?path=" + news.get("link")
                            + "&source=" + news.get("source")
                            + "&domain=" + news.get("domain");
                    String encode = URLEncoder.encode(finalPath, "UTF-8");
                    String url = "https://m.q.qq.com/a/p/1111006861?s=" + encode;
                    event.getSubject().sendMessage(news.get("title") + "\n"+url);
                    //event.getSubject().sendMessage(new LightApp(NewsProvider.genLightJson(news)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            event.getSubject().sendMessage("以上是" + dayRange + "天之内的新闻");
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public void updatePluginData(){
        updatePluginData(pluginData);
    }

    public static List<String> getEnabledList() {
        return enabledList;
    }

    // 初始化插件数据[从外部到内部]
    public void initPluginData(Map<String, Object> config){
        // pluginData.clear();         // 清空
        enabledList.addAll((List<String>)config.get("enabledList"));  // 置入
    }

}
