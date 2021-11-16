package cn.wecuit.robot.plugins.msg;

import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.utils.unirun.UniRunMain;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.ClubInfo;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.JoinClubResult;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    private static String lastNoticeDay = null;

    @SubCmd(keyword = "更新token", desc = "更新token")
    public boolean updateToken(GroupMessageEvent event, CmdList cmdList) {
        String token = cmdList.getFirst();
        pluginData.put("token", token);
        updatePluginData(pluginData);
        event.getSubject().sendMessage("更新token成功！");
        return true;
    }

    @SubCmd(keyword = "添加俱乐部提醒", desc = "在俱乐部有空余时会发送提醒")
    public boolean addNotice(GroupMessageEvent event) {
        List<String> noticeList = (List<String>) pluginData.get("noticeList");
        if (noticeList == null) {
            noticeList = new ArrayList<>();
            pluginData.put("noticeList", noticeList);
        }
        String gid = String.valueOf(event.getSubject().getId());
        if (noticeList.contains(gid)) {
            event.getSubject().sendMessage("已存在！");
            return true;
        }
        noticeList.add(gid);

        updatePluginData(pluginData);
        event.getSubject().sendMessage("添加俱乐部提醒成功！");
        return true;
    }

    @SubCmd(keyword = "删除俱乐部提醒", desc = "删除俱乐部提醒")
    public boolean delNotice(GroupMessageEvent event) {
        List<String> noticeList = (List<String>) pluginData.get("noticeList");
        if (noticeList == null) {
            noticeList = new ArrayList<>();
            pluginData.put("noticeList", noticeList);
        }
        noticeList.remove(String.valueOf(event.getSubject().getId()));

        updatePluginData(pluginData);
        event.getSubject().sendMessage("删除俱乐部提醒成功！");
        return true;
    }

    @SubCmd(keyword = "自动参与俱乐部", desc = "自动参与俱乐部 手机号 密码 校区 关键词\n关键词可选")
    public boolean addAutoJoin(GroupTempMessageEvent event, CmdList cmds) {
        if (event == null) return false;
        Map<String, AutoJoin> autoJoinList = (Map<String, AutoJoin>) pluginData.get("autoJoinList");
        if (autoJoinList == null) {
            autoJoinList = new HashMap<>();
            pluginData.put("autoJoinList", autoJoinList);
        }
        if (cmds.size() < 3) {
            event.getSubject().sendMessage("参数不够");
            return true;
        }
        String phone = cmds.get(0);
        String password = cmds.get(1);
        String location = cmds.get(2);
        String keyword = null;
        if (cmds.size() > 3)
            keyword = cmds.get(3);
        String qqid = String.valueOf(event.getSender().getId());
        AutoJoin autoJoin = new AutoJoin(String.valueOf(event.getGroup().getId()), phone, password, location, keyword);
        String msg;
        if(autoJoinList.containsKey(qqid)){
            log.info("{}", autoJoinList.get(qqid).getPhone());
            msg = "更新成功";
        }else{
            msg = "加入成功";
        }
        autoJoinList.put(qqid, autoJoin);
        if (!autoJoinList.containsKey(qqid)) {
            msg = "加入失败";
        }
        event.getSender().sendMessage(msg);

        updatePluginData(pluginData);
        return true;
    }

    @SubCmd(keyword = "取消自动参与俱乐部", desc = "自动参与俱乐部 ")
    public boolean delAutoJoin(UserMessageEvent event) {
        Map<String, AutoJoin> autoJoinList = (Map<String, AutoJoin>) pluginData.get("autoJoinList");
        if (autoJoinList == null) return true;
        String qqid = String.valueOf(event.getSender().getId());
        if (!autoJoinList.containsKey(qqid)) {
            event.getSender().sendMessage("你没有在自动参与列表");
            return true;
        }
        autoJoinList.remove(qqid);
        if (autoJoinList.containsKey(qqid)) {
            event.getSender().sendMessage("删除失败");
        } else {
            event.getSender().sendMessage("删除成功");
        }
        return true;
    }

    public static void checkClub() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        Date date = new Date(new Date().getTime() + 1000 * 6 * 24 * 60 * 60);
        String today = sdf.format(date);

        // 今天提醒过了
        if (today.equals(lastNoticeDay)) return;

        List<String> noticeList = (List<String>) pluginData.get("noticeList");
        if (noticeList == null) return;

        String token = (String) pluginData.get("token");
        if (token == null) return;

        // 准备群提醒数据
        StringBuilder sb = new StringBuilder("俱乐部空余情况\n");
        List<ClubInfo> availableActivityList = UniRunMain.getAvailableActivityList(token);
        // 无可用
        if (availableActivityList.size() == 0) return;

        for (ClubInfo clubInfo : availableActivityList) {
            sb.append("活动名：").append(clubInfo.getActivityName()).append("\n");
            sb.append("报名人数：").append(clubInfo.getSignInStudent()).append("/").append(clubInfo.getMaxStudent()).append("\n");
            sb.append("可取消(待确认)：").append(clubInfo.getCancelSign() == 1 ? "是\n" : "否\n");
            sb.append("------\n");
        }

        // 群提醒
        for (String gid : noticeList) {
            Group group = RobotMain.getBot().getGroup(Long.parseLong(gid));
            if (group != null)
                group.sendMessage(sb.toString());
            lastNoticeDay = today;
        }

        // 自动加入
        Map<String, AutoJoin> autoJoinList = (Map<String, AutoJoin>) pluginData.get("autoJoinList");
        if (autoJoinList != null) {
            autoJoinList.forEach((qqid, autoJoin) -> {
                // 过滤出包含关键词的俱乐部
                String location = autoJoin.getLocation();
                String keyword = autoJoin.getKeyword();
                List<ClubInfo> keyActList = availableActivityList.stream().filter(activity -> {
                    boolean result = activity.getActivityName().contains(location);
                    if (keyword != null)
                        result = result && activity.getActivityName().contains(keyword);
                    return result;
                }).collect(Collectors.toList());

                // 空
                if (keyActList.size() == 0) return;

                // 取第一个
                Long activityId = keyActList.get(0).getClubActivityId();
                String groupId = autoJoin.getGroupId();
                Group group = RobotMain.getBot().getGroup(Long.parseLong(groupId));
                // 加入
                JoinClubResult joinClubResult = UniRunMain.joinClub(autoJoin.getPhone(), autoJoin.getPassword(), String.valueOf(activityId));

                if (group != null) {
                    NormalMember normalMember = group.get(Long.parseLong(qqid));
                    if (normalMember != null)
                        if (joinClubResult == null) {
                            normalMember.sendMessage("俱乐部参加结果：null\n" + "测试阶段，本次执行后您将被移出参加队列，如有需要请重新发送加入指令");
                        } else
                            normalMember.sendMessage("俱乐部参加结果：" + joinClubResult.getMessage() + "\n" + "测试阶段，本次执行后您将被移出参加队列，如有需要请重新发送加入指令");
                }
            });
            // 清空自动加入
            autoJoinList.clear();
        }
        // 更新
        new UniRunPlugin().updatePluginData(pluginData);
    }

    @Override
    public void initPluginData(Map<String, Object> config) {
        Map<String, AutoJoin> autoJoinList = (Map<String, AutoJoin>)config.get("autoJoinList");
        if(autoJoinList != null){
            autoJoinList = JsonUtil.string2Obj(JsonUtil.obj2String(autoJoinList), new TypeReference<Map<String, AutoJoin>>() {
            });
        }
        config.put("autoJoinList", autoJoinList);
        pluginData.putAll(config);  // 置入
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AutoJoin {
    private String groupId;
    private String phone;
    private String password;
    private String location;
    private String keyword;
}