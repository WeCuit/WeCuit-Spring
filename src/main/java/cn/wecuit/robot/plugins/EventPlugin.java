package cn.wecuit.robot.plugins;

import cn.wecuit.robot.entity.EventType;
import net.mamoe.mirai.event.Event;

/**
 * @Author jiyec
 * @Date 2021/6/15 22:04
 * @Version 1.0
 **/
public interface EventPlugin {
    void init(Event e);

    void handle();
    EventType[] event();
}
