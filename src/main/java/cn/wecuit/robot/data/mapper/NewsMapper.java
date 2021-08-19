package cn.wecuit.robot.data.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author jiyec
 * @Date 2021/5/14 13:40
 * @Version 1.0
 **/
public interface NewsMapper {
    @Insert("INSERT INTO rb_meta VALUES(null, '_transient_timeout-${md5}', unix_timestamp(now()))")
    int addNoticed(@Param("md5")String md5);

    @Delete("DELETE FROM rb_meta WHERE meta_name like '_transient_timeout-%' AND meta_value<UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE))-8*3600")
    int delOutDate();

    @Select("SELECT COUNT(1) FROM rb_meta WHERE meta_name = '_transient_timeout-${md5}'")
    int selCnt(@Param("md5")String md5);
}
