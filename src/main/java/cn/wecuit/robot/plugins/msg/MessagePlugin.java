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
public interface MessagePlugin {

    void init(MessageEvent event, List<String> cmds);

    /**
     * 插件帮助信息
     *
     * @return String 帮助信息
     */
    @NotNull
    String getHelp();

    /**
     * 插件一级指令
     *
     * @return String 指令名
     */
    String getMainCmd();

    /**
     * 插件次级指令
     * 主指令 次级指令
     *
     * @return <次级指令, 指令方法>
     */
    Map<String, String> getSubCmdList();

    /**
     * 插件需注册为一级指令的次级指令
     *
     * @return <指令, 指令方法>
     */
    Map<String, String> getRegisterAsFirstCmd();

    /**
     * 插件需注册为全局指令的次级指令 (不需要匹配指令)
     *
     * @return [指令方法]
     */
    List<String> getGlobalCmd();

    void defaultAction();

    // 初始化插件数据[从外部到内部]
    void initPluginData(Map<String, Object> config);

    // 更新插件数据[从内部到外部]
    void updatePluginData(Map<String, Object> pluginData);
}
