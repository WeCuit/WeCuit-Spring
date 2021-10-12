package cn.wecuit.robot.plugins.msg;

import cn.wecuit.backen.services.NewsService;
import cn.wecuit.backen.utils.SpringUtil;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.provider.NewsProvider;
import cn.wecuit.backen.utils.NewsUtil;
import cn.wecuit.robot.services.RbNewsService;
import lombok.Getter;
import net.mamoe.mirai.event.events.GroupMessageEvent;
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
@RobotPlugin
@MainCmd(keyword = "新闻系统", desc = "Admin:\n\nUser:\n查询 n -- 查询n天之内的新闻")
public class NewsPlugin extends MsgPluginImpl {

    @Value("${wecuit.data-path}")
    String BASE_DATA_PATH;

    private static final List<String> enabledList = new ArrayList<>();
    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("enabledList", enabledList);
    }};

    @SubCmd(keyword = "开启推送", desc = "开启对应群聊的新闻推送功能")
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

    @SubCmd(keyword = "关闭推送", desc = "关闭对应群聊的新闻推送功能")
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

    @SubCmd(keyword = "添加推送目标", desc = "参数[群号]，添加指定群号到推送列表")
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
    @SubCmd(keyword = "推送测试", desc = "在当前群进行一次推送测试")
    public boolean pushTest() throws IOException {
        long senderId = event.getSender().getId();
        if(!checkAdmin(senderId))
            event.getSubject().sendMessage("没有权限");
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

}
