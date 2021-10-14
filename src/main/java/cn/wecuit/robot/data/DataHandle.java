package cn.wecuit.robot.data;

import cn.wecuit.backen.utils.SpringUtil;
import cn.wecuit.robot.plugins.msg.MsgPluginImpl;
import cn.wecuit.robot.pojo.PluginData;
import cn.wecuit.robot.services.RbPluginService;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 初始化部分数据
 *
 * @Author jiyec
 * @Date 2021/5/14 11:55
 * @Version 1.0
 **/
@Slf4j
public class DataHandle {

    // 初始化部分数据
    public static void init(Bot bot){
            RbPluginService rbPluginService = SpringUtil.getBean(RbPluginService.class);

            List<PluginData> allPlugin = rbPluginService.getAllPlugin();
            // 初始化插件数据
            allPlugin.forEach(plugin->{
                String pluginName = plugin.getName();
                Map<String, Object> pluginConfig = plugin.getConfig();

                if(pluginConfig == null)return;

                try {
                    Class<? extends MsgPluginImpl> clazz = (Class<? extends MsgPluginImpl>) Class.forName("cn.wecuit.robot.plugins.msg." + pluginName);

                    Method setPluginData = clazz.getMethod("initPluginData", Map.class);
                    setPluginData.invoke(clazz.newInstance(), pluginConfig);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    boolean b = rbPluginService.delPlugin(pluginName);
                    log.info("未找到插件：{}, 尝试删除，删除状态：{}", pluginName, b);
                    // e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            });

    }

    public static void updatePluginData(Map<String, Object> pluginData){
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);

            RbPluginService rbPluginService = SpringUtil.getBean(RbPluginService.class);
            boolean i = rbPluginService.update(className, pluginData);
            if(!i){
                i = rbPluginService.add(className, pluginData);
            }
            if(!i){
                log.error("updatePluginData Failed!");
            }else{
                log.info("updatePluginData SUCCESS!");
            }


    }

    public static boolean isAdmin(String id){
        return Storage.adminList.contains(id);
    }
}
