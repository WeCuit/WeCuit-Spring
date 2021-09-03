package cn.wecuit.backen.mapper;

import cn.wecuit.backen.bean.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/27 16:44
 * @Version 1.0
 **/
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    @Select("SELECT menu_id FROM wc_role_menu LEFT JOIN wc_menus ON menu_id=wc_menus.id WHERE role_id=#{roleId}")
    List<Long> selectRoleMenus(long roleId);
}
