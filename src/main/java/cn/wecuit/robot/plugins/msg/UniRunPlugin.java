package cn.wecuit.robot.plugins.msg;

import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.entity.CmdList;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import cn.wecuit.robot.utils.unirun.UniRunMain;
import cn.wecuit.robot.utils.unirun.entity.Response;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.ClubInfo;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.JoinClubResult;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.SignInTf;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

    private static String lastExecuteDay = null;
    private static int sleepSecond = 10;

    @SubCmd(keyword = "更新account", desc = "更新account", requireAdmin = true)
    public boolean updateToken(GroupMessageEvent event, CmdList cmdList) {
        String account = cmdList.getFirst();
        if (!account.contains(",")) {
            event.getSubject().sendMessage("格式不正确！\n手机号,密码");
            return true;
        }
        pluginData.put("account", account);
        updatePluginData(pluginData);
        event.getSubject().sendMessage("更新account成功！");
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
        if (!"龙泉".equals(location) && !"航空港".equals(location)) {
            event.getSubject().sendMessage("校区不对\n应该是：“龙泉”或“航空港”\n你输入的是：" + location);
            return true;
        }
        String keyword = null;
        if (cmds.size() > 3)
            keyword = cmds.get(3);
        String qqid = String.valueOf(event.getSender().getId());
        AutoJoin autoJoin = new AutoJoin(String.valueOf(event.getGroup().getId()), phone, password, location, keyword, null);
        String msg;
        if (autoJoinList.containsKey(qqid)) {
            msg = "更新成功";
        } else {
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
    public boolean delAutoJoin(GroupTempMessageEvent event) {
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

        updatePluginData(pluginData);
        return true;
    }

    @SubCmd(keyword = "测试加入俱乐部", requireAdmin = true)
    public void joinTest() {
        clubAutoJoin();
    }

    @SubCmd(keyword = "测试签到", requireAdmin = true)
    public void signTest() {
        signInOrSignBack();
    }

    public static void clubAutoJoin() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        Date date = new Date();
        String today = sdf.format(date);

        log.info("今天：{}", today);
        // 今天执行过了
        if (today.equals(lastExecuteDay)) {
            log.info("执行过了");
            return;
        }

        String account = (String) pluginData.get("account");
        if (account == null) {
            log.info("account为空");
            return;
        }

        String[] split = account.split(",");

        AtomicReference<List<ClubInfo>> availableActivityList = new AtomicReference<>(UniRunMain.getAvailableActivityList(split[0], split[1]));

        // 无可用
        if (availableActivityList.get().size() == 0) {
            log.info("没有可加入的俱乐部");
            return;
        }

        lastExecuteDay = today;

        List<String[]> msgList = new ArrayList<>();

        // 自动加入
        Map<String, AutoJoin> autoJoinList = (Map<String, AutoJoin>) pluginData.get("autoJoinList");
        if (autoJoinList != null && autoJoinList.size() > 0) {

            autoJoinList.forEach((qqid, autoJoin) -> {
                // 过滤出包含关键词的俱乐部
                String location = autoJoin.getLocation();
                String keyword = autoJoin.getKeyword();
                log.info("校区：{} - 关键词：{}", location, keyword);

                String groupId = autoJoin.getGroupId();
                // 加入俱乐部
                Response joinClubResultResponse = UniRunMain.autoJoinClub(autoJoin.getPhone(), autoJoin.getPassword(), location, keyword);

                if(joinClubResultResponse == null)return;

                if (joinClubResultResponse.getCode() == 10000) {

                    JoinClubResult joinClubResult = (JoinClubResult) joinClubResultResponse.getResponse();
                    log.info("加入结果：{}", joinClubResult);
                    if (joinClubResult == null) {
                        msgList.add(new String[]{
                                groupId + "," + qqid,
                                "俱乐部参加结果：" + joinClubResultResponse.getMsg()});
                    } else {
                        msgList.add(new String[]{
                                groupId + "," + qqid,
                                "俱乐部参加结果：" + joinClubResult.getMessage()});
                    }
                } else {
                    msgList.add(new String[]{
                            groupId + "," + qqid,
                            "俱乐部参加结果：" + joinClubResultResponse.getMsg()});
                }
            });

            // 统一发送消息
            for (String[] msg : msgList) {
                String[] group_qq = msg[0].split(",");
                Group group = RobotMain.getBot().getGroup(Long.parseLong(group_qq[0]));
                if (group == null) continue;
                NormalMember normalMember = group.get(Long.parseLong(group_qq[1]));
                if (normalMember == null) continue;
                normalMember.sendMessage(msg[1]);
                try {
                    Thread.sleep(sleepSecond * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void signInOrSignBack() {

        Map<String, AutoJoin> autoJoinList = (Map<String, AutoJoin>) pluginData.get("autoJoinList");
        List<String[]> msgList = new ArrayList<>();
        autoJoinList.forEach((qqid, autoJoin) -> {
            try {
                Response response = UniRunMain.signInOrSignBack(autoJoin.getPhone(), autoJoin.getPassword());

                log.info("俱乐部签到/签退结果：{}", response);
                if (response == null) return;

                String groupId = autoJoin.getGroupId();

                String msg = response.getMsg();
                msgList.add(new String[]{
                        groupId + "," + qqid,
                        "俱乐部签到/签退结果：\n" + msg
                });
                //normalMember.sendMessage("俱乐部签到/签退结果：\n" + msg);

                try {
                    Thread.sleep(2 * 1000L);
                } catch (InterruptedException e) {
                    log.info("睡眠发生异常");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        for (String[] msgData : msgList) {
            String[] ids = msgData[0].split(",");
            Group group = RobotMain.getBot().getGroup(Long.parseLong(ids[0]));
            if(group == null)continue;
            NormalMember normalMember = group.get(Long.parseLong(ids[1]));
            if(normalMember == null)continue;
            normalMember.sendMessage(msgData[1]);
            try {
                Thread.sleep(sleepSecond * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initPluginData(Map<String, Object> config) {
        Map<String, AutoJoin> autoJoinList = (Map<String, AutoJoin>) config.get("autoJoinList");
        if (autoJoinList != null) {
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
    private SignInTf signInTf;
}