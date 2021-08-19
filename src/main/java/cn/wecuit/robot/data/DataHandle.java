package cn.wecuit.robot.data;

import cn.wecuit.mybatis.entity.MyBatis;
import cn.wecuit.robot.data.mapper.PluginMapper;
import cn.wecuit.robot.plugins.msg.MessagePluginImpl;
import cn.wecuit.backen.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
        try (SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()) {
            PluginMapper pluginMapper = sqlSession.getMapper(PluginMapper.class);
            List<HashMap<String, String>> hashMaps = pluginMapper.queryPlugin();
            // 初始化插件数据
            hashMaps.forEach(map->{
                String pluginName = map.get("name");
                String pluginConfigStr = map.get("config");
                Map<String, Object> pluginConfig = JsonUtil.string2Obj(pluginConfigStr, Map.class);

                if(pluginConfig == null)return;

                try {
                    Class<? extends MessagePluginImpl> clazz = (Class<? extends MessagePluginImpl>) Class.forName("cn.wecuit.robot.plugins.msg." + pluginName);

                    Method setPluginData = clazz.getMethod("initPluginData", Map.class);
                    setPluginData.invoke(clazz.newInstance(), pluginConfig);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    int affect = pluginMapper.delPlugin(pluginName);
                    log.info("未找到插件：{}, 尝试删除，影响行数：{}", pluginName, affect);
                    // e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

            });

            sqlSession.commit();
        }
    }

    public static void updatePluginData(Map<String, Object> pluginData){
        String pluginDataStr = JsonUtil.obj2String(pluginData);
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);

        try (SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()) {
            PluginMapper pluginMapper = sqlSession.getMapper(PluginMapper.class);
            int i = pluginMapper.updatePlugin(className, pluginDataStr);
            if(i == 0){
                i = pluginMapper.addPlugin(className, pluginDataStr);
            }
            if(i == 0){
                log.error("updatePluginData Failed!");
            }else{
                log.info("updatePluginData SUCCESS!");
            }
            sqlSession.commit();

        }

    }

    public static boolean isAdmin(String id){
        return Storage.adminList.contains(id);
    }
}
