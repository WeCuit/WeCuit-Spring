package cn.wecuit.robot.plugins;

import net.mamoe.mirai.event.Event;

/**
 * @Author jiyec
 * @Date 2021/6/15 22:06
 * @Version 1.0
 **/
public abstract class EventPluginImpl implements EventPlugin {
    Event event;

    @Override
    public final void init(Event e) {
        this.event = e;
    }

}
