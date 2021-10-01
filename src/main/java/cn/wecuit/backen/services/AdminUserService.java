package cn.wecuit.backen.services;

import cn.wecuit.backen.pojo.AdminUser;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/9/5 8:14
 * @Version 1.0
 **/
public interface AdminUserService {
    boolean register(AdminUser user);
    String[] login(AdminUser user);
    List<String> userMenu(long id);
    AdminUser info(long id);
    boolean modifyInfoById(long id, AdminUser user);
    AdminUser getUserByWxId(String openid);
    AdminUser getUserByQqId(String openid);
    boolean updateInfoById(AdminUser user);
}
