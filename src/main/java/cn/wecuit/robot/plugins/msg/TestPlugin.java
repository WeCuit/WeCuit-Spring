package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.LightApp;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@Slf4j
@RobotPlugin
@MainCmd(keyword = "测试", desc = "测试插件")
public class TestPlugin extends MsgPluginImpl {

    private static final Map<String, Object> pluginData = new HashMap<>();

    @SubCmd(keyword = "二级", desc = "二级指令")
    public boolean testSecond(GroupMessageEvent event, CmdList cmdList){
        event.getSubject().sendMessage("测试指令触发");
        pluginData.put("test", "123");
        updatePluginData(pluginData);
        return true;
    }

    @SubCmd(keyword = "小程序", desc = "二级指令")
    public boolean lightApp(GroupMessageEvent event){
        //{"app":"com.tencent.miniapp_01","view":"view_8C8E89B49BE609866298ADDFF2DBABA4","ver":"1.0.0.19","prompt":"[QQ小程序]We成信大","meta":{"detail_1":{"appid":"1111006861","title":"We成信大","desc":"We成信大","icon":"https:\/\/miniapp.gtimg.cn\/public\/appicon\/6dca4792d15ed27ac7dd436051b46209_200.jpg","preview":"https:\/\/pubminishare-30161.picsz.qpic.cn\/ff2789e9-f028-4e0f-b933-cc3774737920","url":"m.q.qq.com\/a\/s\/f574889c6c0bf474c3d72ebe597dbc36","scene":0,"host":{"uin":1690127128,"nick":"msojocs"},"shareTemplateId":"8C8E89B49BE609866298ADDFF2DBABA4","shareTemplateData":{},"showLittleTail":"","gamePoints":"","gamePointsUrl":""}},"config":{"type":"normal","width":0,"height":0,"forward":0,"autoSize":0,"ctime":1635598375,"token":"6f47df3defbda9bcbdaf1795a12af7a9"}}
        String str = "{\"app\":\"com.tencent.miniapp_01\",\"view\":\"view_8C8E89B49BE609866298ADDFF2DBABA4\",\"ver\":\"1.0.0.19\",\"prompt\":\"[QQ小程序]We成信大\",\"meta\":{\"detail_1\":{\"appid\":\"1111006861\",\"title\":\"We成信大\",\"desc\":\"We成信大123\",\"icon\":\"https:\\/\\/miniapp.gtimg.cn\\/public\\/appicon\\/6dca4792d15ed27ac7dd436051b46209_200.jpg\",\"preview\":\"https:\\/\\/pubminishare-30161.picsz.qpic.cn\\/ff2789e9-f028-4e0f-b933-cc3774737920\",\"url\":\"m.q.qq.com\\/a\\/s\\/f574889c6c0bf474c3d72ebe597dbc36\",\"scene\":0,\"host\":{\"uin\":1690127128,\"nick\":\"msojocs\"},\"shareTemplateId\":\"8C8E89B49BE609866298ADDFF2DBABA4\",\"shareTemplateData\":{},\"showLittleTail\":\"\",\"gamePoints\":\"\",\"gamePointsUrl\":\"\"}},\"config\":{\"type\":\"normal\",\"width\":0,\"height\":0,\"forward\":0,\"autoSize\":0,\"ctime\":1635598375,\"token\":\"6f47df3defbda9bcbdaf1795a12af7a9\"}}";
               //str = "{\"app\":\"com.tencent.miniapp_01\",\"view\":\"view_8C8E89B49BE609866298ADDFF2DBABA4\",\"ver\":\"1.0.0.19\",\"prompt\":\"[QQ小程序]聚力各国智慧，共享创新经验——我校在线举办“第18届创新与管理国际学术会议（ICIM2021）”\",\"meta\":{\"detail_1\":{\"appid\":\"1111006861\",\"title\":\"We成信大\",\"desc\":\"聚力各国智慧，共享创新经验——我校在线举办“第18届创新与管理国际学术会议（ICIM2021）”\",\"icon\":\"https:\\/\\/miniapp.gtimg.cn\\/public\\/appicon\\/6dca4792d15ed27ac7dd436051b46209_200.jpg\"," +
               //        "\"preview\":\"api.oick.cn/random/api.php?type=pc&_t=1635598731288\"," +
               //        "\"url\":\"m.q.qq.com/a/p/1111006861?s=pages%2FarticleView%2FarticleView%3Fpath%3D%2Finfo%2F1963%2F4035.htm%26source%3Dgl%26domain%3Dglxy.cuit.edu.cn\"," +
               //        "\"scene\":0,\"host\":{\"uin\":1690127128,\"nick\":\"msojocs\"},\"shareTemplateId\":\"8C8E89B49BE609866298ADDFF2DBABA4\",\"shareTemplateData\":{},\"showLittleTail\":\"\",\"gamePoints\":\"\",\"gamePointsUrl\":\"\"}},\"config\":{\"type\":\"normal\",\"width\":0,\"height\":0,\"forward\":0,\"autoSize\":0,\"ctime\":1635597993,\"token\":\"18c09e3ce985f36aeffacdfdcb74a30a\"}}";
        event.getSubject().sendMessage(new LightApp(str));
        return true;
    }
    @SubCmd(keyword = "临时", desc = "临时")
    public boolean test(GroupMessageEvent event){
        long senderId = event.getSender().getId();
        event.getGroup().get(senderId).sendMessage("测试");
        return true;
    }
    @SubCmd(keyword = "管理员", desc = "临时", requireAdmin = true)
    public boolean admin(GroupMessageEvent event){
        long senderId = event.getSender().getId();
        event.getSubject().sendMessage("测试");
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
