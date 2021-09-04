package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.User;
import cn.wecuit.backen.mapper.UserMapper;
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
    UserMapper userMapper;

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>(){{
            eq("stu_id", username);
        }});
    }
}
