package cn.wecuit.robot.plugins.msg;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jiyec
 * @Date 2021/6/22 22:18
 * @Version 1.0
 **/
public class AutoRepeatPlugin extends MessagePluginImpl{

    private final static Map<Long, Repeat> data = new HashMap<>();
    @Override
    public String getMainCmd() {
        return null;
    }

    @Override
    public Map<String, String> getSubCmdList() {
        return null;
    }

    @Override
    public List<String> getGlobalCmd() {
        return new ArrayList<String>(){{
            add("repeat");
        }};
    }

    @Override
    public @NotNull String getHelp() {
        return "默认开启，无法关闭\n监控到连续3条相同消息，我会复读一次";
    }

    public boolean repeat(){
        String content = event.getMessage().serializeToMiraiCode();
        long group = event.getSubject().getId();
        Repeat repeat = data.get(group);
        if(repeat == null){
            // 当前群空消息
            data.put(group, new Repeat(content));
        }else if(repeat.content!=null && repeat.content.equals(content)){
            // 内容相同
            if(repeat.cnt.incrementAndGet() == 3){
                event.getSubject().sendMessage(event.getMessage());
            }
            return true;
        }else{
            // 内容不同
            repeat.content = content;
            repeat.cnt.set(1);
        }
        return false;
    }
}

class Repeat{
    String content;
    AtomicInteger cnt = new AtomicInteger(1);

    public Repeat(String content) {
        this.content = content;
    }
}