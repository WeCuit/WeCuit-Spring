package cn.wecuit.backen.mapper;

import org.apache.ibatis.annotations.*;

import java.math.BigInteger;

/**
 * @Author jiyec
 * @Date 2021/5/14 10:39
 * @Version 1.0
 **/
@Mapper
public interface SubMapper {

    @Select("SELECT sub_id FROM wc_sub WHERE user_id=#{uid} AND tpl_id=#{tplId}")
    Long querySubId(@Param("uid")BigInteger uid, @Param("tplId")String tplId);

    @Update("UPDATE wc_sub SET sub_enable=#{enable} WHERE user_id=#{userId} AND tpl_id=#{tplId}")
    int setEnable(@Param("enable") boolean enable, @Param("userId")BigInteger userId, @Param("tplId")String tplId);

    @Insert("INSERT INTO wc_sub(user_id, tpl_id) VALUES(#{uid}, #{tplId})")
    int insertSub(@Param("uid") BigInteger uid, @Param("tplId")String tplId);

    @Delete("DELETE FROM wc_sub WHERE user_id=#{uid}")
    int deleteSub(@Param("uid")BigInteger uid);

    @Update("UPDATE wc_sub SET sub_cnt=sub_cnt+1 WHERE user_id=#{uid} AND tpl_id=#{tplId}")
    int incrCnt(@Param("uid")BigInteger uid, @Param("tplId")String tplId);
}
