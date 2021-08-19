package cn.wecuit.robot.plugins;

import cn.wecuit.robot.PluginHandler;
import cn.wecuit.robot.entity.EventType;
import cn.wecuit.robot.plugins.msg.MessagePluginImpl;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/6/16 8:39
 * @Version 1.0
 **/
public class MessagePlugin extends EventPluginImpl{
    @Override
    public void handle(){

        MessageEvent e = (MessageEvent) event;
        // 机器人发送，忽略
        if(e.getSender().getId() == e.getBot().getId()) return;

        String[] temp = e.getMessage().contentToString().replaceAll("  ", " ").split(" ");

        List<String> cmds = new LinkedList<>(Arrays.asList(temp));

        Class<? extends MessagePluginImpl> pluginClazz;
        Object[] pluginInfo;
        String cmd = cmds.get(0);
        cmds.remove(0);

        Map<String, Class<? extends MessagePluginImpl>> cmd2plugin1 = PluginHandler.cmd2plugin1;
        Map<String, Object[]> cmd2plugin2 = PluginHandler.cmd2plugin2;
        List<Object[]> cmd2plugin3 = PluginHandler.cmd2plugin3;
        if(null != (pluginClazz = cmd2plugin1.get(cmd)))                // 优先查找插件指令
            pluginHandle(pluginClazz, cmds, e);
        else if(null != (pluginInfo = cmd2plugin2.get(cmd))){           // 查找插件子指令
            pluginHandle(pluginInfo, cmds, e);
        }else {
            AtomicBoolean exc = new AtomicBoolean(false);
            cmd2plugin2.forEach((s, m) -> {
                if (exc.get()) return;

                // 正则匹配  查找插件子指令
                try{
                    if(Pattern.compile(s).matcher(cmd).matches()){
                        exc.set(true);
                        pluginHandle(m, cmds, e);
                    }
                }catch (Exception ignored){
                }
            });

            if (!exc.get()) {
                // 其它处理
                for (Object[] objects : cmd2plugin3) {
                    if (pluginHandle(objects, null, e))
                        break;
                }
            }
        }
    }

    // 注册为一级指令的处理
    private boolean pluginHandle(Object[] pluginInfo, List<String> cmds, MessageEvent event){
        try {
            Class<? extends MessagePluginImpl> clazz = (Class<? extends MessagePluginImpl>)pluginInfo[0];

            // 获取 INSTANCE
            cn.wecuit.robot.plugins.msg.MessagePlugin plugin = clazz.newInstance();

            // 初始化
            plugin.init(event, cmds);

            // 调用
            return (boolean)((Method)pluginInfo[1]).invoke(plugin);

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        return false;
    }
    // 普通指令处理
    private void pluginHandle(Class<? extends MessagePluginImpl> pluginClazz, List<String> cmds, MessageEvent event){

        if(cmds.size()==0)return;

        try {
            // 获取 INSTANCE
            cn.wecuit.robot.plugins.msg.MessagePlugin plugin = pluginClazz.newInstance();

            plugin.init(event, cmds);

            String subCmd = cmds.get(0);
            cmds.remove(0);

            // 请求帮助
            if("?".equals(subCmd) || "？".equals(subCmd)){
                event.getSubject().sendMessage(plugin.getHelp());
                return;
            }

            Map<String, String> subCmdList = plugin.getSubCmdList();

            if(!subCmdList.containsKey(subCmd)) {
                event.getSubject().sendMessage("这格式似乎不对呀~(>_<。)＼");
                return;
            }

            String cmd = subCmdList.get(subCmd);
            Method method = pluginClazz.getMethod(cmd);
            method.invoke(plugin);

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        // 获取 Plugin对象
    }


    @Override
    public EventType[] event() {
        return new EventType[]{EventType.GroupMessageEvent};
    }
}
