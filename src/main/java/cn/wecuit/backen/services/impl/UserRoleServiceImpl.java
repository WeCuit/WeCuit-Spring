package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.mapper.UserRoleMapper;
import cn.wecuit.backen.services.UserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author jiyec
 * @Date 2021/9/3 19:55
 * @Version 1.0
 **/
@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Resource
    UserRoleMapper userRoleMapper;

    @Override
    public boolean deleteByRoleId(long roleId) {
        int i = userRoleMapper.deleteById(roleId);
        return i == 1;
    }
}
