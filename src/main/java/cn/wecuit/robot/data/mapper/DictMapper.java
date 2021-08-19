package cn.wecuit.robot.data.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/5/14 14:19
 * @Version 1.0
 **/
public interface DictMapper {
    @Insert("INSERT INTO rb_dict VALUES(null, #{key}, #{value})")
    int addItem(@Param("key")String key, @Param("value")String msg);

    @Select("SELECT dict_value FROM rb_dict WHERE dict_key like '%${key}%'")
    List<String> getMsgList(@Param("key")String key);

    @Select({"<script>" +
            "SELECT dict_value FROM rb_dict WHERE" +
            "<foreach item=\"item\" index=\"index\" collection=\"list\"\n" +
            "      open=\"\" separator=\" OR \" close=\"\">" +
            " dict_key like '%${item}%' " +
            "</foreach>" +
            "</script>"})
    List<String> getMsgList2(@Param("list")List<String> keys);
}
