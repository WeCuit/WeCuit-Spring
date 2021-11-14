package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.utils.unirun.UniRunMain;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.ClubInfo;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/11/14 21:52
 * @Version 1.0
 **/
@Slf4j
@RobotPlugin
@MainCmd(keyword = "UR", desc = "UniRun插件")
public class UniRunPlugin extends MsgPluginImpl {
    private static final Map<String, Object> pluginData = new HashMap<>();

    @SubCmd(keyword = "更新token", desc = "更新token")
    public boolean updateToken(GroupMessageEvent event, CmdList cmdList){
        String token = cmdList.getFirst();
        pluginData.put("token", token);
        updatePluginData(pluginData);
        event.getSubject().sendMessage("更新token成功！");
        return true;
    }

    @SubCmd(keyword = "添加俱乐部提醒", desc = "添加俱乐部提醒")
    public boolean addNotice(GroupMessageEvent event, CmdList cmdList){
        List<String> noticeList = (List<String>) pluginData.get("noticeList");
        if(noticeList == null) {
            noticeList = new ArrayList<>();
            pluginData.put("noticeList", noticeList);
        }
        noticeList.add(String.valueOf(event.getSubject().getId()));

        updatePluginData(pluginData);
        event.getSubject().sendMessage("添加俱乐部提醒成功！");
        return true;
    }

    @SubCmd(keyword = "删除俱乐部提醒", desc = "删除俱乐部提醒")
    public boolean delNotice(GroupMessageEvent event, CmdList cmdList){
        List<String> noticeList = (List<String>) pluginData.get("noticeList");
        if(noticeList == null) {
            noticeList = new ArrayList<>();
            pluginData.put("noticeList", noticeList);
        }
        noticeList.remove(String.valueOf(event.getSubject().getId()));

        updatePluginData(pluginData);
        event.getSubject().sendMessage("删除俱乐部提醒成功！");
        return true;
    }

    // 从第0分钟开始每隔10分钟执行一次
    @Scheduled(cron = "0 20/2 7 * * ?")
    public void checkClub(){
        List<String> noticeList = (List<String>) pluginData.get("noticeList");
        if(noticeList == null)return;

        String token = (String) pluginData.get("token");
        if(token == null)return ;

        StringBuilder sb = new StringBuilder("俱乐部空余\n");
        List<ClubInfo> availableActivityList = UniRunMain.getAvailableActivityList(token);
        for (ClubInfo clubInfo : availableActivityList) {
            sb.append("活动名：").append(clubInfo.getActivityName()).append("\n");
            sb.append("报名人数：").append(clubInfo.getSignInStudent()).append("/").append(clubInfo.getMaxStudent()).append("\n");
            sb.append("------\n");
        }
        for (String gid : noticeList) {
            Group group = RobotMain.getBot().getGroup(Long.parseLong(gid));
            if(group != null)
                group.sendMessage(sb.toString());
        }
    }

    @Override
    public void initPluginData(Map<String, Object> config) {
        pluginData.putAll(config);  // 置入
    }
}
