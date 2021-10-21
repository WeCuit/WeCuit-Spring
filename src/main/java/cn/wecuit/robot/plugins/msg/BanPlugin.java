package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.data.DataHandle;
import cn.wecuit.robot.entity.MainCmd;
import cn.wecuit.robot.entity.RobotPlugin;
import cn.wecuit.robot.entity.SubCmd;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author jiyec
 * @Date 2021/6/10 12:46
 * @Version 1.0
 **/
@Slf4j
@RobotPlugin
@MainCmd(keyword = "屏蔽系统", desc = "消息屏蔽")
public class BanPlugin extends MsgPluginImpl {

    private static final Map<String, List<String>> banRuleItems = new HashMap<>();
    // private static final List<String> enabledList = new ArrayList<>();

    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("banRuleItems", banRuleItems);
        // put("enabledList", enabledList);
    }};

    @SubCmd(keyword = "添加违禁表达式")
    public void addRuleItem(){
        if(!isAdmin())return;

        String cmd = cmds.get(0);
        String groupId = Long.toString(event.getSubject().getId());
        List<String> rule = banRuleItems.computeIfAbsent(groupId, k -> new ArrayList<>());
        try {
            Pattern.compile(cmd);
            rule.add(cmd);
            updatePluginData();
            MessageReceipt messageReceipt = event.getSubject().sendMessage("添加规则成功\n" + cmd);
            new MessageRecall(messageReceipt, 10).start();
        }catch (PatternSyntaxException e){
            event.getSubject().sendMessage("表达式异常，请重新检查");
        }
    }

    @SubCmd(keyword = "查看违禁表达式")
    public void viewRuleItem(){
        if(!isAdmin())return;

        List<String> list = banRuleItems.get(Long.toString(event.getSubject().getId()));
        StringBuilder msg = new StringBuilder("以下为违禁规则：\n");
        for (int i = 0; i < list.size(); i++) {
            msg.append(i).append(". ").append(list.get(i)).append("\n");
        }
        event.getSubject().sendMessage(msg.toString());
    }

    @SubCmd(keyword = "删除违禁表达式")
    public void delRuleItem(){
        if(!isAdmin())return;

        String id = cmds.get(0);
        String groupId = Long.toString(event.getSubject().getId());
        List<String> rules = banRuleItems.get(groupId);
        if(rules == null)
        {
            event.getSubject().sendMessage("没有可以删除的规则");
            return;
        }
        try {
            int i = Integer.parseInt(id);
            String remove = rules.remove(i);
            updatePluginData();
            event.getSubject().sendMessage("删除规则成功：\n" + remove);
        }catch (Exception e){
            event.getSubject().sendMessage("删除规则失败：\n" + e.getMessage());
        }

    }

    @SubCmd(keyword = "清空违禁表达式")
    public void clearRuleItem(){
        if(!isAdmin())return;

        String groupId = Long.toString(event.getSubject().getId());
        banRuleItems.remove(groupId);
        updatePluginData();
        event.getSubject().sendMessage("已清空");

    }

    @SubCmd(keyword = "测试违禁表达式", desc = "测试违禁表达式 内容")
    public void testRuleItem(){
        if(!isAdmin())return;

        String s = event.getMessage().contentToString();
        String groupId = Long.toString(event.getSubject().getId());
        List<String> rules = banRuleItems.get(groupId);
        for (String ruleItem : rules) {
            if(Pattern.compile(ruleItem).matcher(s).find()){
                event.getSubject().sendMessage("匹配成功：" + ruleItem);
                return;
            }
        }
        event.getSubject().sendMessage("没有匹配");
    }

    @SubCmd(keyword = "")
    public boolean msgCheck(GroupMessageEvent event){
        Group group = event.getBot().getGroup(event.getSubject().getId());
        MemberPermission botPermission = group.getBotPermission();
        // 机器人是普通用户
        if(botPermission.getLevel() == 0)return false;
        MemberPermission senderPermission = group.get(event.getSender().getId()).getPermission();
        // 发送者是管理员
        if(senderPermission.getLevel() > 0)return false;

        String s = event.getMessage().contentToString();
        String groupId = Long.toString(event.getSubject().getId());
        List<String> rules = banRuleItems.get(groupId);
        if(rules != null)
            for (String ruleItem : rules) {
                if(Pattern.compile(ruleItem).matcher(s).find()){
                    Mirai.getInstance().recallMessage(event.getBot(), event.getSource());
                    return true;
                }
            }
        return false;
    }

    @Override
    public void initPluginData(Map<String, Object> config) {
        Object banRuleItems = config.get("banRuleItems");
        if(banRuleItems instanceof Map) {
            Map<String, List<String>> items = (Map<String, List<String>>) banRuleItems;
            if (items != null)
                BanPlugin.banRuleItems.putAll(items);
        }
        // List<String> enabledList1 = (List<String>) config.get("enabledList");
        // enabledList.addAll(enabledList1);
    }

    public void updatePluginData(){
        updatePluginData(pluginData);
    }

    private boolean isAdmin(){
        String id = Long.toString(event.getSender().getId());
        if(!DataHandle.isAdmin(id))event.getSubject().sendMessage("你的权限不够啊~(>_<。)＼");
        return DataHandle.isAdmin(id);
    }
}
