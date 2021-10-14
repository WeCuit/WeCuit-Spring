package cn.wecuit.robot.plugins;

import cn.wecuit.robot.entity.EventType;
import cn.wecuit.robot.entity.RobotEventHandle;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.plugins.msg.SwitchPlugin;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;

/**
 * @Author jiyec
 * @Date 2021/6/16 22:42
 * @Version 1.0
 **/

@RobotPlugin
public class JoinPlugin {
    // 处理入群事件
    @RobotEventHandle(event = EventType.MemberJoinRequestEvent)
    public void handle(Event event) {
        MemberJoinRequestEvent e = (MemberJoinRequestEvent) event;
        String gId = Long.toString(e.getGroup().getId());
        String uId = Long.toString(e.getFromId());
        boolean userBan = SwitchPlugin.isUserBan(gId, uId);
        if(userBan){
            e.reject(true);
        }
    }

}
