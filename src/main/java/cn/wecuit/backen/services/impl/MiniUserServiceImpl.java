package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.mapper.MiniUserMapper;
import cn.wecuit.backen.pojo.AdminUser;
import cn.wecuit.backen.mapper.AdminUserMapper;
import cn.wecuit.backen.pojo.MiniUser;
import cn.wecuit.backen.services.MiniUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author jiyec
 * @Date 2021/9/4 17:24
 * @Version 1.0
 **/
@Service
public class MiniUserServiceImpl implements MiniUserService {
    @Resource
    MiniUserMapper miniUserMapper;

    @Override
    public MiniUser getUserById(long id) {
        return miniUserMapper.selectById(id);
    }
}
