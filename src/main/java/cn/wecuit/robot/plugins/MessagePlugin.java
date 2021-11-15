package cn.wecuit.robot.plugins;

import cn.wecuit.robot.PluginHandler;
import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.EventType;
import cn.wecuit.robot.entity.RobotEventHandle;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.plugins.msg.MsgPlugin;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author jiyec
 * @Date 2021/6/16 8:39
 * @Version 1.0
 **/
@Slf4j
@RobotPlugin
public class MessagePlugin {
    @RobotEventHandle(event = {EventType.GroupMessageEvent, EventType.UserMessageEvent, EventType.FriendMessageEvent})
    public void handleMsg(MessageEvent event) {
        // 机器人发送，忽略
        if (event.getSender().getId() == event.getBot().getId()) return;

        // 去除多余空格
        String[] temp = event.getMessage().contentToString().replaceAll("  ", " ").split(" ");

        // 指令集转为List
        CmdList cmdList = new CmdList(Arrays.asList(temp));

        String cmd = cmdList.get(0);
        cmdList.remove(0);

        Map<String, Object> cmd2plugin = PluginHandler.cmd2plugin;
        List<Method> cmd2plugin3 = PluginHandler.cmd2plugin3;
        Set<String> keys = cmd2plugin.keySet();
        Object action = null;
        for (String key : keys) {
            // 正则匹配指令
            try {
                Pattern compile = Pattern.compile(key);
                if(compile.matcher(cmd).matches()){
                    action = cmd2plugin.get(key);
                    break;
                }
            }catch (PatternSyntaxException e){
                log.error("正则预编译指令失败：{}", key);
                //e.printStackTrace();
            }
        }
        if (action == null) {
            // 没有找到一级指令，交给全局监听方法处理
            log.info("未找到指令，应该交给全局监听方法处理");
            cmd2plugin3.forEach(m->{
                try {
                    m.invoke(m.getDeclaringClass().newInstance(), event);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        if(event instanceof GroupMessageEvent){
            GroupMessageEvent ge = (GroupMessageEvent) event;
            if(ge.getGroup().getBotMuteRemaining() != 0)return;
        }

        try {
            if (action instanceof Method) {
                // 一级指令对应方法
                Method method = (Method) action;
                Parameter[] parameters = method.getParameters();
                Object[] args = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    String name = parameters[i].getType().getSimpleName();
                    if(event instanceof GroupMessageEvent && EventType.GroupMessageEvent.name().equals(name)){
                        args[i] = event;
                    }else if(event instanceof UserMessageEvent && EventType.UserMessageEvent.name().equals(name)){
                        args[i] = event;
                    }else if("CmdList".equals(name)){
                        args[i] = cmdList;
                    }else {
                        log.info("无法识别的参数类型：{} - {}", name, parameters[i].getType().getName());
                        return;
                    }
                }
                method.invoke(method.getDeclaringClass().newInstance(), args);
            } else if (action instanceof Map) {
                Map<String, Object> subCmd = ((Map<String, Object>) action);
                String c = cmdList.get(0);
                cmdList.remove(0);
                // 帮助指令
                if ("?".equals(c) || "？".equals(c)) {
                    Object data = subCmd.get("?");
                    event.getSubject().sendMessage("帮助信息：\n" + data);
                    return;
                }
                // 可能为操作指令
                Object ac = subCmd.get(c);
                // 指令不存在
                if (ac == null) {
                    event.getSubject().sendMessage("指令有误");
                    return;
                }
                // 指令匹配
                if (ac instanceof Method) {
                    // 指令对应方法
                    Method method = (Method) ac;
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        String name = parameters[i].getType().getSimpleName();
                        if(event instanceof GroupMessageEvent && EventType.GroupMessageEvent.name().equals(name)){
                            args[i] = event;
                        }else if(event instanceof UserMessageEvent && EventType.UserMessageEvent.name().equals(name)){
                            args[i] = event;
                        }else if("CmdList".equals(name)){
                            args[i] = cmdList;
                        }else{
                            log.info("无法识别的参数类型：{} - {}", name, parameters[i].getType().getName());
                            return;
                        }
                    }
                    MsgPlugin o = (MsgPlugin) method.getDeclaringClass().newInstance();
                    method.invoke(o, args);
                }
            }

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

    }

}
