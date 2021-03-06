package cn.wecuit.backen.mapper;

import cn.wecuit.backen.pojo.AdminUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/15 10:00
 * @Version 1.0
 **/
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    // 根据openid或sid 查询 userid
    @Select("SELECT user_id FROM wc_users WHERE `user_sid`=#{sid};")
    BigInteger queryUIdBySId(@Param("sid")String sid);

    @Select("SELECT user_id FROM wc_users WHERE `user_${client}id`=#{openid};")
    BigInteger queryUIdByOpenId(@Param("client")String client, @Param("openid")String openid);

    @Update("UPDATE `wc_users` SET `user_sid` = #{sId}, `user_spass` = #{sPass} WHERE `wc_users`.`user_id` = #{uid};")
    int updateStuInfoByUId(@Param("uid")BigInteger uid, @Param("sId")String sId, @Param("sPass")String sPass);

    @Options(useGeneratedKeys = true, keyProperty = "uid.uid", keyColumn = "user_id")
    @Insert("INSERT INTO wc_users (`user_${client}id`, user_sid, user_spass) VALUES(#{openid}, #{sId}, #{sPass});")
    int addUser(@Param("uid") Map<String, BigInteger> uid, @Param("client")String client, @Param("openid")String openid, @Param("sId")String sId, @Param("sPass")String sPass);

    @Delete("DELETE FROM wc_users WHERE `${client}id`=#{openid}")
    int delUser(@Param("client")String client, @Param("openid")String openid);

    @Select("SELECT path from wc_user_role a LEFT JOIN wc_roles b ON a.role_id=b.id LEFT JOIN wc_role_menu c ON b.id=c.role_id LEFT JOIN wc_menus d ON c.menu_id=d.id WHERE a.user_id=#{userId}")
    List<String> getUserMenuPath(long userId);

}
