package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.Menu;
import cn.wecuit.backen.mapper.MenuMapper;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.MenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/27 18:42
 * @Version 1.0
 **/
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    MenuMapper menuMapper;

    @Override
    public List<Menu> list() {
        return menuMapper.selectList(null);
    }

    @Override
    public boolean modify(Menu menu) {
        return menuMapper.updateById(menu) == 1;
    }

    @Override
    public boolean add(Menu menu) {
        menu.setId(null);
        return 1 == menuMapper.insert(menu);
    }

    @Override
    public boolean delete(long id) {
        return 1 == menuMapper.deleteById(id);
    }
}
