package cn.wecuit.robot.plugins;

import cn.wecuit.robot.common.Nudge;
import cn.wecuit.robot.entity.EventType;
import net.mamoe.mirai.event.events.NudgeEvent;

/**
 * @Author jiyec
 * @Date 2021/6/15 22:02
 * @Version 1.0
 **/
public class NudgePlugin extends EventPluginImpl{

    @Override
    public void handle() {
        Nudge.nudge((NudgeEvent) event);
    }

    @Override
    public EventType[] event() {
        return new EventType[]{EventType.NudgeEvent};
    }
}
