package cn.wecuit.robot.plugins.msg;

import cn.wecuit.backen.services.NewsService;
import cn.wecuit.robot.provider.NewsProvider;
import cn.wecuit.backen.utils.NewsUtil;
import lombok.Getter;
import net.mamoe.mirai.message.data.LightApp;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
public class NewsPlugin extends MessagePluginImpl {

    @Value("${wecuit.data-path}")
    String BASE_DATA_PATH;

    @Resource
    NewsService newsService;

    private static final List<String> enabledList = new ArrayList<>();
    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("enabledList", enabledList);
    }};

    // 二级指令
    @Getter
    private final Map<String, String> subCmdList = new HashMap<String, String>(){{
        put("开启推送", "enablePush");
        put("关闭推送", "disablePush");
        put("推送测试", "pushTest");
        put("添加推送目标", "addPushTarget");
        put("查询", "query");
    }};

    // 需要注册为一级指令的 指令
    @Getter
    private final Map<String, String> registerAsFirstCmd = new HashMap<String, String>(){{

    }};

    // 本插件一级指令
    @Override
    public String getMainCmd() {
        return "新闻系统";
    }

    @Override
    public @NotNull String getHelp() {
        return "Admin:\n开启/关闭推送 -- 开启/关闭对应群聊的新闻推送功能\n推送测试 -- 在当前群进行一次推送测试\n添加推送目标 群号 -- 添加指定群号到推送列表\n\nUser:\n查询 n -- 查询n天之内的新闻";
    }

    public boolean enablePush(){
        long senderId = event.getSender().getId();
        if(!checkAdmin(senderId))
            event.getSubject().sendMessage("没有权限");

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

    public boolean disablePush(){
        long senderId = event.getSender().getId();
        if(!checkAdmin(senderId))
            event.getSubject().sendMessage("没有权限");

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

    public boolean addPushTarget(){
        long senderId = event.getSender().getId();
        if(!checkAdmin(senderId))
            event.getSubject().sendMessage("没有权限");

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
    public boolean pushTest() throws IOException {
        long senderId = event.getSender().getId();
        if(!checkAdmin(senderId))
            event.getSubject().sendMessage("没有权限");
        String subjectId = Long.toString(event.getSubject().getId());
        newsService.newsNotice(new ArrayList<String>(){{add(subjectId);}});
        return true;
    }

    public boolean query(){
        String temp = BASE_DATA_PATH + "/WeCuit";
        String cachePath = temp + "/cache";
        String listPath = cachePath + "/news/list";

        try{
            int dayRange = 1;
            if(cmds.size() > 0)
                dayRange = Integer.parseInt(cmds.get(0));

            List<Map<String, String>> todayNews = NewsUtil.getLatestNews(listPath, dayRange);

            if(todayNews.size() == 0) {
                event.getSubject().sendMessage("没有" + dayRange + "天之内的新闻");
                return true;
            }

            todayNews.forEach(news->{
                try {
                    event.getSubject().sendMessage(new LightApp(NewsProvider.genLightJson(news)));
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
    public static boolean checkAdmin(Long id){
        return id == 1690127128L;
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

    @Override
    public List<String> getGlobalCmd() {
        return null;
    }
}
