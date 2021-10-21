package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.eventHandle.RepeatEventJava;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@RobotPlugin
@MainCmd(keyword = "复读系统", desc = "User:\n开启 - 进入复读模式\nstop - 退出复读")
public class RepeatPlugin extends MsgPluginImpl {

    private static final List<Long> repeatList = new ArrayList<>();

    @SubCmd(keyword = "开启")
    public boolean enableMode(GroupMessageEvent event){
        long id = event.getSubject().getId();
        if(repeatList.contains(id))return true;
        repeatList.add(id);
        event.getBot().getEventChannel().registerListenerHost(new RepeatEventJava(id));
        return true;
    }

}
