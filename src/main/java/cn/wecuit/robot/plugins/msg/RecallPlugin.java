package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.data.Storage;
import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@Slf4j
@RobotPlugin
@MainCmd(keyword = "撤回系统", desc = Storage.name + "撤回 - 撤回" + Storage.name + "发出的最后一条消息")
public class RecallPlugin extends MsgPluginImpl {

    private static final Map<String, Object> pluginData = new HashMap<>();

    @SubCmd(keyword = Storage.name + "撤回", regAsMainCmd = true)
    public boolean doRecall(GroupMessageEvent event){
        long id = event.getSubject().getId();
        MessageReceipt message = Storage.getMessage(id);
        if(message!=null) {
            try {
                message.recall();
            }catch (Exception e){
                e.printStackTrace();
                MessageReceipt receipt = event.getSubject().sendMessage("失败，没有可撤回的消息（本消息将在5秒后自动撤回）");
                new MessageRecall(receipt, 5000).start();
            }
        }
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
