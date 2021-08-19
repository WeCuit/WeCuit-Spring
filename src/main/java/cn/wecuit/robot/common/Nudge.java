package cn.wecuit.robot.common;

import cn.wecuit.robot.data.Storage;
import cn.wecuit.robot.entity.QQUser;
import net.mamoe.mirai.event.events.NudgeEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/8 14:18
 * @Version 1.0
 **/
public class Nudge {
    private static final Map<Long, QQUser> users = new HashMap<>();

    public static void nudge(NudgeEvent event){
        // 戳的不是机器人，忽略
        if(event.getTarget().getId() != event.getBot().getId())return;

        long from = event.getFrom().getId();
        if(from == event.getBot().getId())return;

        QQUser qqUser = users.get(from);
        if(qqUser == null){
            // 新增用户
            qqUser = new QQUser();
            qqUser.setId(from);
            users.put(from, qqUser);
        }

        String[] msgs = {
                "回戳",
                new At(event.getFrom().getId()) + "不要再戳啦，很痛的好不好≧ ﹏ ≦",
                new At(event.getFrom().getId()) + "再戳我，我就要生气啦(╯▔皿▔)╯",
                new At(event.getFrom().getId()) + "哼，本大小姐1分钟之内都不会理你啦\n(￢︿̫̿￢☆)"
        };
        int count = qqUser.addNudgeCount(60);

        long subjectId = event.getSubject().getId();
        if(count == 3)Storage.addIgnore(subjectId + "," + from, 60);
        if(count == -1)return ;

        String msg = msgs[count];

        if("回戳".equals(msg)){
            event.getFrom().nudge().sendTo(event.getSubject());
        }else {
            event.getSubject().sendMessage(MiraiCode.deserializeMiraiCode(msg));
        }
    }
}
