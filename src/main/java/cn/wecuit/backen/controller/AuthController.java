package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.Menu;
import cn.wecuit.backen.services.MenuService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/28 6:18
 * @Version 1.0
 **/

@Api(value = "授权")
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    MenuService menuService;

    @GetMapping("/menus")
    public List<Menu> list(){
        return menuService.list();
    }

    @PatchMapping("/menus/{id}")
    public boolean edit(@PathVariable long id, @RequestBody Menu menu){
        menu.setId(id);
        return menuService.modify(menu);
    }

    @PostMapping("/menus")
    public boolean add(@RequestBody Menu menu){
        return menuService.add(menu);
    }

    @DeleteMapping("/menus/{id}")
    public boolean delete(@PathVariable long id){
        return menuService.delete(id);
    }
}
