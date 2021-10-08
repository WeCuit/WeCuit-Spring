package cn.wecuit.robot.services.impl;

import cn.wecuit.robot.mapper.PluginMapper;
import cn.wecuit.robot.pojo.PluginData;
import cn.wecuit.robot.services.RbPluginService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/7 20:05
 * @Version 1.0
 **/
@Service
public class RbPluginServiceImpl implements RbPluginService {
    @Resource
    PluginMapper pluginMapper;

    @Override
    public List<PluginData> getAllPlugin() {
        return pluginMapper.selectList(null);
    }

    @Override
    public boolean delPlugin(String name) {

        int d = pluginMapper.delete(new QueryWrapper<PluginData>() {{
            eq("name", name);
        }});
        return d == 1;
    }

    @Override
    public boolean update(String name, Map<String, Object> config) {

        int update = pluginMapper.update(new PluginData() {{
            setConfig(config);
        }}, new QueryWrapper<PluginData>() {{
            eq("name", name);
        }});
        return update == 1;
    }

    @Override
    public boolean add(String name, Map<String, Object> config) {
        int insert = pluginMapper.insert(new PluginData(null, name, config));
        return insert == 1;
    }
}
