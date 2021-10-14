package cn.wecuit.robot.plugins.msg;

import cn.wecuit.backen.utils.SpringUtil;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.provider.WSeg;
import cn.wecuit.robot.services.RbDictService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.code.MiraiCode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@Slf4j
@RobotPlugin
@MainCmd(keyword = "聊天功能", desc = "Admin:\n发送[聊天功能 开启]可开启指定群聊的聊天功能")
public class ChatPlugin extends MsgPluginImpl {

    private static final List<String> enabledList = new ArrayList<>();
    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("enabledList", enabledList);
    }};

    @SubCmd(keyword = "")
    public boolean chat(){

        String msg = event.getMessage().contentToString();

        // 群号
        String subjectId = Long.toString(event.getSubject().getId());

        // 是否开启聊天模式
        if(!enabledList.contains(subjectId))return false;


            log.info("开始分词");
            List<String> keys = WSeg.seg(msg);

            log.info("分词结果: {}", keys);
            if(keys.size()>2)
                keys = keys
                        .stream().filter(predicate->predicate.length()>1)
                        .collect(Collectors.toList());

            keys.add(msg);
            log.info("最终查询依据: {}", keys);

        RbDictService dictService = SpringUtil.getBean(RbDictService.class);
        List<String> msgList = dictService.getByKeyword(keys);

            if(msgList.size() > 0) {
                int i = (int) (Math.random() * (msgList.size()));
                msg = msgList.get(i);

                event.getSubject().sendMessage(MiraiCode.deserializeMiraiCode(msg));
            }

        return false;
    }

    @SubCmd(keyword = "开启")
    public boolean enableMode(){
        String subjectId = Long.toString(event.getSubject().getId());
        boolean allowChat = enabledList.contains(subjectId);
        if(allowChat)
            event.getSubject().sendMessage("已经是开启状态了");
        else{
            enabledList.add(subjectId);
            event.getSubject().sendMessage("已开启");
            updatePluginData();
        }
        return true;
    }

    @SubCmd(keyword = "关闭")
    public boolean disableMode(){
        String subjectId = Long.toString(event.getSubject().getId());
        boolean allowChat = enabledList.contains(subjectId);
        if(!allowChat)
            event.getSubject().sendMessage("已经是关闭状态了");
        else {
            enabledList.remove(subjectId);
            event.getSubject().sendMessage("已关闭");
            updatePluginData();
        }
        return true;
    }

    public void updatePluginData(){
        updatePluginData(pluginData);
    }

    // 初始化插件数据[从外部到内部]
    public void initPluginData(Map<String, Object> config){
        // pluginData.clear();         // 清空
        enabledList.addAll((List<String>)config.get("enabledList"));  // 置入
    }
}
