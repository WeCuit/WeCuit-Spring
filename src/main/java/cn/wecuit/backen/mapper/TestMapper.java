package cn.wecuit.backen.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/5/11 11:02
 * @Version 1.0
 **/
public interface TestMapper {
    @Select("SELECT * FROM test_table")
    public List<HashMap> selectAll();
}
