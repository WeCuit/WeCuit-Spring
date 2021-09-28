package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.AdminUser;
import cn.wecuit.backen.bean.MiniUser;

/**
 * @Author jiyec
 * @Date 2021/9/4 17:24
 * @Version 1.0
 **/
public interface UserService {
    AdminUser getUserByUsername(String username);
}
