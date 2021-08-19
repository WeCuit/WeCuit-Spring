package cn.wecuit.robot;

import cn.wecuit.robot.data.Storage;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.MessagePostSendEvent;
import net.mamoe.mirai.message.MessageReceipt;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:30
 * @Version 1.0
 **/
@Slf4j
public class MainHandleJava {

    public static void init(Bot bot){
        // 注册插件
        // registerPlugin();
        PluginHandler.register();

        // 注册事件
        registerEvent(bot);
    }

    private static void registerEvent(Bot bot){
        // 监听所有事件
        bot.getEventChannel().subscribeAlways(Event.class, PluginHandler::event);

        // 监听机器人发送消息
        bot.getEventChannel().subscribeAlways(MessagePostSendEvent.class, e->{
            long targetId = e.getTarget().getId();
            MessageReceipt receipt = e.getReceipt();
            Storage.addOldMessage(targetId, receipt);
        });
    }

}
