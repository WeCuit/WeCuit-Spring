package cn.wecuit.robot.plugins;

import cn.wecuit.robot.common.Nudge;
import cn.wecuit.robot.entity.EventType;
import cn.wecuit.robot.entity.RobotEventHandle;
import cn.wecuit.robot.entity.RobotPlugin;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.NudgeEvent;

/**
 * @Author jiyec
 * @Date 2021/6/15 22:02
 * @Version 1.0
 **/
@RobotPlugin
public class NudgePlugin {

    @RobotEventHandle(event = EventType.NudgeEvent)
    public void nudgeHandle(Event event){
        Nudge.nudge((NudgeEvent) event);
    }

}
