package cn.wecuit.backen.services;

import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.pojo.MiniUser;

/**
 * @Author jiyec
 * @Date 2021/9/4 17:24
 * @Version 1.0
 **/
public interface MiniUserService {
    MiniUser getUserById(long id);
    MiniUser getUserByOpenid(String openid, MiniType type);
    MiniUser regUserByOpenid(String openid, MiniType type);
    boolean bindStudent(MiniUser user);
    boolean bindMini(MiniUser user);
}
