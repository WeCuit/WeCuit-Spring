package cn.wecuit.robot.plugins;

import cn.wecuit.robot.PluginHandler;
import cn.wecuit.robot.entity.*;
import cn.wecuit.robot.plugins.msg.AdminPlugin;
import cn.wecuit.robot.plugins.msg.MsgPlugin;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

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
    /**
     *
     * @param event 事件
     */
    @RobotEventHandle(event = {EventType.GroupMessageEvent, EventType.UserMessageEvent, EventType.GroupTempMessageEvent})
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
                    callMethod(event, cmdList, m);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        // 群消息，但被禁言
        if(event instanceof GroupMessageEvent){
            GroupMessageEvent ge = (GroupMessageEvent) event;
            if(ge.getGroup().getBotMuteRemaining() != 0)return;
        }

        try {
            if (action instanceof Method) {
                // 一级指令对应方法
                callMethod(event, cmdList, (Method) action, true);
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
                    callMethod(event, cmdList, (Method) ac, true);
                }
            }

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param event 消息事件
     * @param cmdList 指令列表
     * @param method 指令方法
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void callMethod(MessageEvent event, CmdList cmdList, Method method) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        callMethod(event, cmdList, method, false);
    }

    /**
     *
     * @param event 消息事件
     * @param cmdList 指令列表
     * @param method 指令方法
     * @param notice 指令发送渠道不正确时，是否提示
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void callMethod(MessageEvent event, CmdList cmdList, Method method, boolean notice) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Parameter[] parameters = method.getParameters();
        SubCmd annotation = method.getAnnotation(SubCmd.class);

        // 管理权限检测
        if(annotation.requireAdmin() && !AdminPlugin.isSuperAdmin(String.valueOf(event.getSender().getId()))){
            event.getSubject().sendMessage("权限不足");
            return;
        }

        MsgPlugin o = (MsgPlugin) method.getDeclaringClass().newInstance();
        if(parameters.length > 0) {
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                String name = parameters[i].getType().getSimpleName();
                // MessageEvent 接受多来源的消息
                if (EventType.MessageEvent.name().equals(name)
                        || (event.getClass().getSimpleName().equals(name))) {
                    args[i] = event;
                } else if ("CmdList".equals(name)) {
                    args[i] = cmdList;
                } else {
                    log.info("无法识别的参数类型：{} - {}", name, parameters[i].getType().getName());
                    if(notice)event.getSubject().sendMessage("该指令只能通过如下渠道发送：" + EventType.valueOf(name).getMessage());
                    return;
                }
            }
            method.invoke(o, args);
        }else{
            method.invoke(o);
        }
    }

}
