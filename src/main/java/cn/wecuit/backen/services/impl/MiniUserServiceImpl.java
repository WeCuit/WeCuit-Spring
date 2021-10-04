package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.entity.MiniType;
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

    @Override
    public MiniUser getUserByOpenid(String openid, MiniType type) {

        return miniUserMapper.selectOne(new QueryWrapper<MiniUser>() {{
            if(type == MiniType.QQ)
                eq("qq_id", openid);
            else if(type == MiniType.WX)
                eq("wx_id", openid);
            select("id", "wx_id", "qq_id", "stu_id");
        }});
    }

    @Override
    public MiniUser regUserByOpenid(String openid, MiniType type) {
        MiniUser miniUser = new MiniUser() {{
            if (type == MiniType.QQ)
                setQqId(openid);
            else if (type == MiniType.WX)
                setWxId(openid);
        }};
        int i = miniUserMapper.insert(miniUser);
        if(i == 1)
            return miniUser;
        else
            return null;
    }

    @Override
    public boolean bindStudent(MiniUser user) {
        int i = miniUserMapper.updateById(user);
        return i == 1;
    }
}
