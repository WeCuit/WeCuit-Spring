package cn.wecuit.robot.data.mapper;

import org.apache.ibatis.annotations.*;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/23 12:08
 * @Version 1.0
 **/
public interface PictureMapper {
    @Insert("INSERT INTO rb_picture(p_img_id, p_info, p_level) VALUES(#{imgid}, #{info}, #{level})")
    int addPic(@Param("imgid")String id, @Param("info")String detail, @Param("level")int level);

    @Insert("INSERT INTO rb_meta VALUES(null, 'picture_cnt_${level}', 1)")
    int addMeta(@Param("level")int level);

    @Update("UPDATE rb_meta SET meta_value=meta_value+${cnt} WHERE meta_name='picture_cnt_${level}'")
    int increCntByLevel(@Param("cnt")int cnt, @Param("level")int level);

    // @Result(column = "cnt", javaType = Integer.class)
    @Select("SELECT FLOOR(RAND()*SUM(ct.cnt)) AS pos FROM (SELECT meta_value AS cnt FROM `rb_meta` WHERE meta_name LIKE 'picture\\_cnt\\_${level}') ct")
    Integer queryPosBylevel(@Param("level")String level);

    @Select("SELECT p_img_id AS id, p_info AS info FROM `rb_picture` WHERE p_level LIKE #{level} LIMIT ${pos},1")
    Map<String, String> getByPosLevel(@Param("level")String level, @Param("pos") int pos);
}
