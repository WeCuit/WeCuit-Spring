package cn.wecuit.robot.services;

import cn.wecuit.robot.pojo.PluginData;

import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/7 20:04
 * @Version 1.0
 **/
public interface RbPluginService {
    List<PluginData> getAllPlugin();
    boolean delPlugin(String name);
    boolean update(String name, Map<String, Object> config);
    boolean add(String name, Map<String, Object> config);
}
