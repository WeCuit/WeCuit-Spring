package cn.wecuit.robot.data.mapper;

import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/5/14 12:27
 * @Version 1.0
 **/
public interface PluginMapper {
    @Select("SELECT plugin_name AS name, plugin_config AS config FROM rb_plugin")
    List<HashMap<String, String>> queryPlugin();

    @Insert("INSERT INTO rb_plugin(plugin_id, plugin_name, plugin_config) VALUES(null, #{name}, #{config})")
    int addPlugin(@Param("name")String name,  @Param("config")String config);

    @Update("UPDATE rb_plugin SET plugin_config=#{config} WHERE plugin_name=#{name}")
    int updatePlugin(@Param("name") String name, @Param("config") String config);

    @Delete("DELETE FROM rb_plugin WHERE plugin_name=#{name}")
    int delPlugin(@Param("name") String name);

}
