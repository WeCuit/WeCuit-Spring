package cn.wecuit.backen.mapper;

/**
 * @Author jiyec
 * @Date 2021/9/3 19:55
 * @Version 1.0
 **/

import cn.wecuit.backen.bean.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    @Select("SELECT name from wc_user_role a LEFT JOIN wc_roles b ON a.role_id=b.id WHERE user_id=#{userId}")
    List<String> getRoleByUserId(long userId);
}
