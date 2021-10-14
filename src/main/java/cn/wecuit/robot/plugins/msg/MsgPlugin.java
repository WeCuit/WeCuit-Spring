package cn.wecuit.robot.plugins.msg;

import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:40
 * @Version 1.0
 **/
public interface MsgPlugin {

    void init(MessageEvent event, List<String> cmds);

    // 初始化插件数据[从外部到内部]
    void initPluginData(Map<String, Object> config);

    // 更新插件数据[从内部到外部]
    void updatePluginData(Map<String, Object> pluginData);
}
