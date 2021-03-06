package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.data.DataHandle;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:40
 * @Version 1.0
 **/
public interface MsgPlugin {

    // 初始化插件数据[从外部到内部]
    void initPluginData(Map<String, Object> config);

    // 更新插件数据[从内部到外部]
    static void updatePluginData(Map<String, Object> pluginData) {
        DataHandle.updatePluginData(pluginData);
    }
}
