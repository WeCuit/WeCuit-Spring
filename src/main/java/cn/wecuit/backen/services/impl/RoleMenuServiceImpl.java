package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.RoleMenu;
import cn.wecuit.backen.mapper.RoleMenuMapper;
import cn.wecuit.backen.services.RoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/28 9:30
 * @Version 1.0
 **/
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
    @Override
    public boolean removeList(List<RoleMenu> list) {
        return this.remove(new QueryWrapper<RoleMenu>(){{
            for (RoleMenu roleMenu : list) {
                or().eq("role_id", roleMenu.getRoleId()).eq("menu_id", roleMenu.getMenuId());
            }
        }});
    }
}
