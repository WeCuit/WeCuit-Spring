package cn.wecuit.robot.plugins.msg;

import cn.wecuit.robot.MainHandleJava;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
public class MenuPlugin extends MessagePluginImpl {

    private static final List<String> pluginList;
    private static final StringBuilder menuStr = new StringBuilder();

    static{
        pluginList = new LinkedList<String>(){{
            String plugins = MainHandleJava.class.getResource("plugins/msg").getPath();
            String[] list = new File(plugins).list((dir, name) -> !name.contains("$") && !name.contains("Message") && name.endsWith("Plugin.class"));

            for (String s : list) {
                add("msg." + s.substring(0, s.indexOf("Plugin")));
            }
        }};

        AtomicInteger i = new AtomicInteger();
        menuStr.append("详细说明在下面列表名称中加第二参数“？”，中间记得加空格哟~(>_<。)比如：「菜单系统 ?」\n--------------\n");
        pluginList.forEach(p->{
            try {
                Class<? extends MessagePluginImpl> clazz = (Class<? extends MessagePluginImpl>) Class.forName("cn.wecuit.robot.plugins." + p + "Plugin");

                // 获取 INSTANCE
                // Field instance = clazz.getField("INSTANCE");

                // 获取 Plugin对象
                // BasePlugin plugin = (BasePlugin)instance.get(p);
                MessagePlugin plugin = clazz.newInstance();
                String cmdStr = plugin.getMainCmd();
                if(cmdStr == null)return ;

                // 增加  [指令 ---> 对象] 关联
                if(i.getAndIncrement() % 2 == 0)
                    menuStr.append("--" + cmdStr + "--");
                else
                    menuStr.append("||--" + cmdStr + "--\n");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    // 二级指令
    @Getter
    private final Map<String, String> subCmdList = new HashMap<String, String>(){{
        put("菜单", "getMenu");
    }};
    // 需要注册为一级指令的 指令
    @Getter
    private final Map<String, String> registerAsFirstCmd = new HashMap<String, String>(){{
        put("菜单", "getMenu");
    }};

    // 本插件一级指令
    @Override
    public String getMainCmd() {
        return "菜单系统";
    }

    @Override
    public @NotNull String getHelp() {
        return "菜单提列举了当前系统所具备的功能";
    }

    @Override
    public List<String> getGlobalCmd() {
        return null;
    }

    public boolean getMenu(){
        event.getSubject().sendMessage(menuStr.toString());
        return true;
    }

}
