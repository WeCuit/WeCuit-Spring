package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.data.Storage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
public class AdminPlugin extends MessagePluginImpl {

    private static final List<String> adminList = Storage.adminList;
    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("adminList", adminList);
    }};

    // 二级指令
    @Getter
    private final Map<String, String> subCmdList = new HashMap<String, String>(){{
        put("添加管理", "addAdmin");
        put("删除管理", "delAdmin");
        put("管理列表", "listAdmin");
    }};
    // 需要注册为一级指令的 指令
    @Getter
    private final Map<String, String> registerAsFirstCmd = new HashMap<String, String>(){{

    }};

    // 本插件一级指令
    @Override
    public String getMainCmd() {
        return "管理系统";
    }

    @Override
    public @NotNull String getHelp() {
        return "添加管理 id --- 添加新管理\n删除管理 id --- 删除旧管理";
    }

    @Override
    public List<String> getGlobalCmd() {
        return null;
    }

    public boolean addAdmin(){
        if(!isSuperAdmin()){
            event.getSubject().sendMessage("权限不足");
            return true;
        }
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
    public boolean delAdmin(){
        if(!isSuperAdmin()){
            event.getSubject().sendMessage("权限不足");
            return true;
        }
        String newAdmin = cmds.get(0);
        adminList.remove(newAdmin);
        updatePluginData();
        event.getSubject().sendMessage("OK");
        return true;
    }
    public boolean listAdmin(){
        if(!isSuperAdmin()){
            event.getSubject().sendMessage("权限不足");
            return true;
        }
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
            al = al.stream().distinct().collect(Collectors.toList());
            adminList.addAll(al);
        }
    }
    void updatePluginData(){
        updatePluginData(pluginData);
    }
    private boolean isSuperAdmin(){
        long id = event.getSender().getId();
        return id == 1690127128L;
    }
}
