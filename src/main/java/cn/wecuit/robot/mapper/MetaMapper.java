package cn.wecuit.robot.mapper;

import cn.wecuit.robot.pojo.Meta;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author jiyec
 * @Date 2021/10/7 19:50
 * @Version 1.0
 **/
@Mapper
public interface MetaMapper extends BaseMapper<Meta> {
    @Delete("DELETE FROM rb_meta WHERE name like '_transient_timeout-%' AND value<UNIX_TIMESTAMP(CAST(SYSDATE()AS DATE))-8*3600")
    int delNoticedNewsBeforeToday();
}
