package cn.wecuit.backen.services;

import cn.wecuit.backen.pojo.Role;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/28 7:11
 * @Version 1.0
 **/
public interface RoleService {
    List<Role> list();
    boolean add(Role role);
    boolean delete(long id);
    boolean modify(Role role);
}
