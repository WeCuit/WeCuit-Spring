package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@Slf4j
@RobotPlugin
@MainCmd(keyword = "测试系统", desc = "测试插件")
public class TestPlugin extends MsgPluginImpl {

    private static final Map<String, Object> pluginData = new HashMap<>();

    @SubCmd(keyword = "二级", desc = "二级指令")
    public boolean testSecond(){
        event.getSubject().sendMessage("测试指令触发");
        pluginData.put("test", "123");
        updatePluginData(pluginData);
        return true;
    }

    // 初始化插件数据[从外部到内部]
    public void initPluginData(Map<String, Object> config){
        pluginData.putAll(config);  // 置入
    }

    public void updatePluginData() {
        super.updatePluginData(pluginData);
    }
}
