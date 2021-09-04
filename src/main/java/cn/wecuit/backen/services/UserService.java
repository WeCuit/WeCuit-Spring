package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.User;

/**
 * @Author jiyec
 * @Date 2021/9/4 17:24
 * @Version 1.0
 **/
public interface UserService {
    User getUserByUsername(String username);
}
