package cn.wecuit.robot.eventHandle;

import cn.wecuit.robot.data.Storage;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @Author jiyec
 * @Date 2021/5/16 18:01
 * @Version 1.0
 **/
public class RepeatEventJava extends SimpleListenerHost {
    private final long fromId;
    private boolean first = true;

    public RepeatEventJava(long id){

        Storage.addIgnore(Long.toString(id), 0);
        fromId = id;
    }
    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception){
        // 处理事件处理时抛出的异常
        exception.printStackTrace();
    }
    @NotNull
    @EventHandler
    public ListeningStatus onMessage(@NotNull MessageEvent event) throws Exception { // 可以抛出任何异常, 将在 handleException 处理

        if(first) {
            event.getSubject().sendMessage("已开启复读模式\n发送[stop]关闭");
            first = false;
            return ListeningStatus.LISTENING;
        }
        long id = event.getSubject().getId();

        // 不是开启复读的对象
        if(id != fromId)return ListeningStatus.LISTENING; // 表示继续监听事件

        String msg = event.getMessage().contentToString();

        if("stop".equals(msg)){
            event.getSubject().sendMessage("停止复读");
            Storage.delIgnore(Long.toString(fromId));
            return ListeningStatus.STOPPED; // 表示停止监听事件
        }else
            event.getSubject().sendMessage(event.getMessage());

        return ListeningStatus.LISTENING; // 表示停止监听事件
    }
}
