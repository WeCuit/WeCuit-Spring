package cn.wecuit.robot.mapper;

import cn.wecuit.robot.pojo.RbDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/10/8 10:58
 * @Version 1.0
 **/
@Mapper
public interface DictMapper extends BaseMapper<RbDict> {
    @Select({"<script>" +
            "SELECT dict_value FROM rb_dict WHERE" +
            "<foreach item=\"item\" index=\"index\" collection=\"list\"\n" +
            "      open=\"\" separator=\" OR \" close=\"\">" +
            " dict_key like '%${item}%' " +
            "</foreach>" +
            "</script>"})
    List<String> getMsgList2(@Param("list")List<String> keys);
}
