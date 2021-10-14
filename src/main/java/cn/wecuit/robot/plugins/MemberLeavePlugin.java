package cn.wecuit.robot.plugins;

import cn.wecuit.robot.data.Storage;
import cn.wecuit.robot.entity.EventType;
import cn.wecuit.robot.entity.RobotEventHandle;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.plugins.msg.SwitchPlugin;
import net.mamoe.mirai.event.events.MemberLeaveEvent;

/**
 * @Author jiyec
 * @Date 2021/6/15 22:31
 * @Version 1.0
 **/
@RobotPlugin
public class MemberLeavePlugin {

    @RobotEventHandle(event = EventType.MemberLeaveEvent)
    public void handleEvent(MemberLeaveEvent event){
        String gId = Long.toString(event.getGroupId());
        String msg = event.getMember().getNick();
        boolean notice = SwitchPlugin.quitNoticeList.contains(gId);
        boolean ban = SwitchPlugin.quitBanList.contains(gId);
        if (ban || notice)
            msg += " 静静地离开了(；′⌒`)";

        if(ban) {
            msg += Storage.name + "用小本本记起来了，再也进不来了!";
            SwitchPlugin.banNewUser(gId, Long.toString(event.getMember().getId()));
        }

        if (ban || notice)
            event.getGroup().sendMessage(msg);
    }

}
