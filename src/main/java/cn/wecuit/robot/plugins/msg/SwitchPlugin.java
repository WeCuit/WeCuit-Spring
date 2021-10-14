package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.data.Storage;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @Author jiyec
 * @Date 2021/6/16 20:04
 * @Version 1.0
 **/
@RobotPlugin
@MainCmd(keyword = "开关系统", desc = "Admin:\\n\" +\n" +
        "                \"退群提醒 开启/关闭 --- 有人退群时提醒\\n\" +\n" +
        "                \"退群屏蔽 开启/关闭 --- 有人退群后加入黑名单")
public class SwitchPlugin extends MsgPluginImpl {
    public final static List<String> quitNoticeList = new LinkedList<>();
    public final static List<String> quitBanList = new LinkedList<>();
    private final static Map<String, List<String>> banUser = new HashMap<>();

    @SubCmd(keyword = "退群提醒")
    public void quitNotice(){
        quitHandle(quitNoticeList);
    }
    @SubCmd(keyword = "退群屏蔽")
    public void quitBan(){
        quitHandle(quitBanList);
    }
    private void quitHandle(List<String> list) {
        String id = Long.toString(event.getSender().getId());
        if(!Storage.adminList.contains(id)){
            event.getSubject().sendMessage("你没有权限。。。");
            return;
        }
        switch (cmds.get(0)){
            case "开启":
                list.add(Long.toString(event.getSubject().getId()));
                break;
            case "关闭":
                list.remove(Long.toString(event.getSubject().getId()));
                break;
            default:
                return;
        }
        updatePluginData();
        event.getSubject().sendMessage("OK");
    }
    public static void banNewUser(String groupId, String userId){
        List<String> list = banUser.computeIfAbsent(groupId, k -> new ArrayList<>());
        list.add(userId);
    }
    public static boolean isUserBan(String groupId, String userId){
        List<String> list = banUser.get(groupId);
        if (list == null)return false;
        return list.contains(userId);
    }

    @Override
    public void initPluginData(Map<String, Object> config) {
        List<String> quitNotice = ( List<String>)config.get("quitNotice");
        if(quitNotice != null)
        quitNoticeList.addAll(quitNotice);
        List<String> qb = ( List<String>)config.get("quitBan");
        if(qb != null)
            quitBanList.addAll(qb);
        Map<String, List<String>> bu = (Map<String, List<String>>)config.get("banUser");
        if(bu != null)
            banUser.putAll(bu);
    }

    void updatePluginData(){
        updatePluginData(new HashMap<String, Object>(){{
            put("quitNotice", quitNoticeList);
            put("quitBan", quitBanList);
            put("banUser", banUser);
        }});
    }
}
