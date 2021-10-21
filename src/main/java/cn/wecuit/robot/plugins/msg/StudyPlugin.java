package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.eventHandle.StudyEventKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import lombok.Getter;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

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
@MainCmd(keyword = "学习系统", desc = "Admin:\n发送[学习模式 开启]可在指定群聊开启学习模式\n\n普通用户：\n发送[*学习 xxx]可对xxx进行学习")
public class StudyPlugin extends MsgPluginImpl {

    private static final List<String> enabledList = new ArrayList<>();
    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("enabledList", enabledList);
    }};

    @SubCmd(keyword = "开启")
    public boolean enableMode(GroupMessageEvent event){

        long senderId = event.getSender().getId();
        if(!checkAdmin(senderId)) {
            event.getSubject().sendMessage("没有权限");
            return true;
        }

        String subjectId = Long.toString(event.getSubject().getId());
        boolean allowStudy = enabledList.contains(subjectId);

        if(allowStudy){
            event.getSubject().sendMessage("已经是开启状态了");
        }else {
            event.getSubject().sendMessage("已开启本群学习模式\n可以发送以下格式消息让我学习\n*学习 关键词");
            enabledList.add(subjectId);
        }
        return true;
    }

    @SubCmd(keyword = "关闭")
    public boolean disableMode(GroupMessageEvent event){

        long senderId = event.getSender().getId();
        if(!checkAdmin(senderId)) {
            event.getSubject().sendMessage("没有权限");
            return true;
        }

        String subjectId = Long.toString(event.getSubject().getId());
        boolean allowStudy = enabledList.contains(subjectId);

        if(allowStudy) {
            event.getSubject().sendMessage("已关闭本群学习模式");
            enabledList.remove(subjectId);
        }else{
            event.getSubject().sendMessage("已经是关闭状态了");
        }

        return true;
    }

    @SubCmd(keyword = "*学习", regAsMainCmd = true)
    public void study(GroupMessageEvent event) {

        if(enabledList.contains(Long.toString(event.getSubject().getId())))
            // 交由Kotlin处理
            StudyEventKt.INSTANCE.handle(event, new Continuation<Unit>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {

                }
            });
        else
            event.getSubject().sendMessage("未开启学习功能");
    }

    public static boolean checkAdmin(Long id){
        return id == 1690127128L;
    }

    // 初始化插件数据[从外部到内部]
    public void initPluginData(Map<String, Object> config){
        enabledList.addAll((List<String>)config.get("enabledList"));  // 置入
    }

    public void updatePluginData() {
        super.updatePluginData(pluginData);
    }
}
