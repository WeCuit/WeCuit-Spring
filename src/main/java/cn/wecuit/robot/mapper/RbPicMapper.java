package cn.wecuit.robot.mapper;

import cn.wecuit.robot.pojo.RbPicture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/8 12:08
 * @Version 1.0
 **/
@Mapper
public interface RbPicMapper extends BaseMapper<RbPicture> {
    @Select("SELECT FLOOR(RAND()*SUM(ct.cnt)) AS pos FROM (SELECT meta_value AS cnt FROM `rb_meta` WHERE meta_name LIKE 'picture\\_cnt\\_${level}') ct")
    Integer queryPosBylevel(@Param("level")String level);

    @Select("SELECT p_img_id AS id, p_info AS info FROM `rb_picture` WHERE p_level LIKE #{level} LIMIT ${pos},1")
    Map<String, String> getByPosLevel(@Param("level")String level, @Param("pos") int pos);
}
