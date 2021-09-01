package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/28 9:29
 * @Version 1.0
 **/
public interface RoleMenuService  extends IService<RoleMenu>  {
    boolean removeList(List<RoleMenu> list);
}
