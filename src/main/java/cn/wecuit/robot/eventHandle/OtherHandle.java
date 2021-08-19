package cn.wecuit.robot.eventHandle;

import cn.wecuit.robot.data.Storage;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/5/19 15:16
 * @Version 1.0
 **/
@Slf4j
public class OtherHandle {

    public static boolean isIgnored(MessageEvent event) {
        long subjectId = event.getSubject().getId();
        long senderId = event.getSender().getId();
        String key = subjectId + "," + senderId;
        Long ignore1 = Storage.getIgnore(key);
        if(null == ignore1) {
            Long ignore2 = Storage.getIgnore(key);
            if (null == ignore2) return false;
            else if (ignore2 == 0) return true;
            else return ignore2 > new Date().getTime() / 1000;
        }
        else if (ignore1 == 0) return true;
        else return ignore1 > new Date().getTime() / 1000;
    }

}
