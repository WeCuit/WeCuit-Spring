package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.eventHandle.RepeatEventJava;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
public class RepeatPlugin extends MessagePluginImpl {

    private static final List<Long> repeatList = new ArrayList<>();

    // 二级指令
    @Getter
    private final Map<String, String> subCmdList = new HashMap<String, String>(){{
        put("开启", "enableMode");
    }};

    // 需要注册为一级指令的 指令
    @Getter
    private final Map<String, String> registerAsFirstCmd = new HashMap<String, String>(){{

    }};

    // 本插件一级指令
    @Override
    public String getMainCmd() {
        return "复读系统";
    }

    @Override
    public @NotNull String getHelp() {
        return "User:\n开启 - 进入复读模式\nstop - 退出复读";
    }

    @Override
    public List<String> getGlobalCmd() {
        return null;
    }

    public boolean enableMode(){
        long id = event.getSubject().getId();
        if(repeatList.contains(id))return true;
        repeatList.add(id);
        event.getBot().getEventChannel().registerListenerHost(new RepeatEventJava(id));
        return true;
    }

}
