package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.AdminUser;
import cn.wecuit.backen.bean.MiniUser;
import cn.wecuit.backen.mapper.AdminUserMapper;
import cn.wecuit.backen.services.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author jiyec
 * @Date 2021/9/4 17:24
 * @Version 1.0
 **/
@Service
public class UserServiceImpl implements UserService {
    @Resource
    AdminUserMapper adminUserMapper;

    @Override
    public AdminUser getUserByUsername(String username) {
        return adminUserMapper.selectOne(new QueryWrapper<AdminUser>(){{
            eq("stu_id", username);
        }});
    }
}
