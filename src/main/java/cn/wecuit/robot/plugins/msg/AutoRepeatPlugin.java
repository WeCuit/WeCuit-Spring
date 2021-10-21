package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jiyec
 * @Date 2021/6/22 22:18
 * @Version 1.0
 **/
@RobotPlugin
@MainCmd(keyword = "复读系统", desc = "机器人的复读操作")
public class AutoRepeatPlugin extends MsgPluginImpl {

    private final static Map<Long, Repeat> data = new HashMap<>();

    @SubCmd(keyword = "", desc="默认开启，无法关闭\n监控到连续3条相同消息，我会复读一次")
    public boolean repeat(GroupMessageEvent event){
        String content = event.getMessage().serializeToMiraiCode();
        long group = event.getSubject().getId();
        Repeat repeat = data.get(group);
        if(repeat == null){
            // 当前群空消息
            data.put(group, new Repeat(content));
        }else if(repeat.content!=null && repeat.content.equals(content)){
            // 内容相同
            if(repeat.cnt.incrementAndGet() == 3){
                event.getSubject().sendMessage(event.getMessage());
            }
            return true;
        }else{
            // 内容不同
            repeat.content = content;
            repeat.cnt.set(1);
        }
        return false;
    }
}

class Repeat{
    String content;
    AtomicInteger cnt = new AtomicInteger(1);

    public Repeat(String content) {
        this.content = content;
    }
}