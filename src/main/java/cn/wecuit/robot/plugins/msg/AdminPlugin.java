package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.data.Storage;
import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@RobotPlugin
@MainCmd(keyword = "管理系统", desc = "机器人管理系统")
public class AdminPlugin extends MsgPluginImpl {

    private static final List<String> adminList = Storage.adminList;
    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("adminList", adminList);
    }};

    @SubCmd(keyword = "添加管理", desc = "添加机器人管理员", requireAdmin = true)
    public boolean addAdmin(GroupMessageEvent event, CmdList cmds){
        String newAdmin = cmds.get(0);
        if(!adminList.contains(newAdmin)) {
            adminList.add(newAdmin);
            updatePluginData();
            event.getSubject().sendMessage("OK");
        }else{
            event.getSubject().sendMessage("已存在");
        }
        return true;
    }
    @SubCmd(keyword = "删除管理", desc = "删除指定机器人管理员", requireAdmin = true)
    public boolean delAdmin(GroupMessageEvent event, CmdList cmds){
        String newAdmin = cmds.get(0);
        adminList.remove(newAdmin);
        updatePluginData();
        event.getSubject().sendMessage("OK");
        return true;
    }
    @SubCmd(keyword = "列出管理", desc = "列出所有机器人管理员", requireAdmin = true)
    public boolean listAdmin(GroupMessageEvent event){
        StringBuilder list = new StringBuilder();
        for (String s : adminList) {
            list.append(s).append("\n");
        }
        event.getSubject().sendMessage(list.toString());
        return true;
    }

    // 初始化插件数据[从外部到内部]
    public void initPluginData(Map<String, Object> config){
        List<String> al = (List<String>) config.get("adminList");
        if(al != null){
            // 将默认管理员合并
            al.addAll(adminList);
            // 唯一化
            al = al.stream().distinct().collect(Collectors.toList());
            // 清空默认
            adminList.clear();
            // 加入管理
            adminList.addAll(al);
        }
    }
    void updatePluginData(){
        updatePluginData(pluginData);
    }
    public static boolean isSuperAdmin(String id){
        return adminList.contains(id);
    }
}
