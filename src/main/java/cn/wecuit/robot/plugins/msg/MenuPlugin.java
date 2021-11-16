package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.PluginHandler;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@RobotPlugin
@MainCmd(keyword = "菜单系统", desc = "菜单提列举了当前系统所具备的功能")
public class MenuPlugin extends MsgPluginImpl {

    @SubCmd(keyword = "菜单", regAsMainCmd = true, desc = "一级指令")
    public boolean getMenu(MessageEvent event){
        final StringBuilder menuStr = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        menuStr.append("详细说明在下面列表名称中加第二参数“？”，中间记得加空格哟~(>_<。)比如：「菜单系统 ?」\n--------------\n");
        PluginHandler.cmd2plugin.forEach((k, v)->{

            if(v instanceof Method)return;
            // 增加  [指令 ---> 对象] 关联
            if(i.getAndIncrement() % 2 == 0)
                menuStr.append("--").append(k).append("--");
            else
                menuStr.append("||--").append(k).append("--\n");
        });
        event.getSubject().sendMessage(menuStr.toString());
        return true;
    }

    @Override
    public void initPluginData(Map<String, Object> config) {

    }
}
