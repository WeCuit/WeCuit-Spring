package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.Menu;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/27 18:42
 * @Version 1.0
 **/
public interface MenuService {
    /**
     * 获取所有菜单
     * @return
     */
    List<Menu> list();

    /**
     * 修改菜单
     * @param menu
     * @return
     */
    boolean modify(Menu menu);
    boolean add(Menu menu);
    boolean delete(long id);
}
