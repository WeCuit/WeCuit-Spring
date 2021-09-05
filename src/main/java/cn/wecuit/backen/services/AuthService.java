package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.User;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/9/5 8:14
 * @Version 1.0
 **/
public interface AuthService {
    String[] login(User user);
    List<String> userMenu(long id);
}
