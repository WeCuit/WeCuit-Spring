package cn.wecuit.robot.plugins;

import cn.wecuit.robot.entity.EventType;
import cn.wecuit.robot.plugins.msg.SwitchPlugin;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;

/**
 * @Author jiyec
 * @Date 2021/6/16 22:42
 * @Version 1.0
 **/
public class JoinPlugin extends EventPluginImpl{
    @Override
    public void handle() {
        MemberJoinRequestEvent e = (MemberJoinRequestEvent) event;
        String gId = Long.toString(e.getGroup().getId());
        String uId = Long.toString(e.getFromId());
        boolean userBan = SwitchPlugin.isUserBan(gId, uId);
        if(userBan){
            e.reject(true);
        }
    }

    @Override
    public EventType[] event() {
        return new EventType[]{EventType.MemberJoinRequestEvent};
    }
}
