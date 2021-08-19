package cn.wecuit.robot.plugins;

import cn.wecuit.robot.data.Storage;
import cn.wecuit.robot.entity.EventType;
import cn.wecuit.robot.plugins.msg.SwitchPlugin;
import net.mamoe.mirai.event.events.MemberLeaveEvent;

/**
 * @Author jiyec
 * @Date 2021/6/15 22:31
 * @Version 1.0
 **/
public class MemberLeavePlugin extends EventPluginImpl{
    @Override
    public void handle() {

        MemberLeaveEvent e = (MemberLeaveEvent) event;
        String gId = Long.toString(e.getGroupId());
        String msg = e.getMember().getNick();
        boolean notice = SwitchPlugin.quitNoticeList.contains(gId);
        boolean ban = SwitchPlugin.quitBanList.contains(gId);
        if (ban || notice)
            msg += " 静静地离开了(；′⌒`)";

        if(ban) {
            msg += Storage.name + "用小本本记起来了，再也进不来了!";
            SwitchPlugin.banNewUser(gId, Long.toString(e.getMember().getId()));
        }

        if (ban || notice)
            e.getGroup().sendMessage(msg);
    }

    @Override
    public EventType[] event() {
        return new EventType[]{EventType.Quit};
    }
}
