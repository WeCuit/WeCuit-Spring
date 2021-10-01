package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.pojo.Role;
import cn.wecuit.backen.pojo.RoleMenu;
import cn.wecuit.backen.pojo.UserRole;
import cn.wecuit.backen.mapper.RoleMapper;
import cn.wecuit.backen.mapper.RoleMenuMapper;
import cn.wecuit.backen.mapper.UserRoleMapper;
import cn.wecuit.backen.services.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/28 7:11
 * @Version 1.0
 **/
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    RoleMapper roleMapper;
    @Resource
    RoleMenuMapper roleMenuMapper;
    @Resource
    UserRoleMapper userRoleMapper;

    @Override
    public List<Role> list() {
        return roleMapper.selectList(null);
    }

    @Override
    public boolean add(Role role) {
        role.setId(null);
        return 1 == roleMapper.insert(role);
    }

    @Override
    public boolean delete(long id) {
        roleMenuMapper.delete(new QueryWrapper<RoleMenu>(){{
            eq("role_id", id);
        }});
        userRoleMapper.delete(new QueryWrapper<UserRole>(){{
            eq("role_id", id);
        }});
        return 1 == roleMapper.deleteById(id);
    }

    @Override
    public boolean modify(Role role) {
        return 1 == roleMapper.updateById(role);
    }

}
