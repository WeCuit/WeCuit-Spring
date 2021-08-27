package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.Menu;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.MenuService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/27 16:53
 * @Version 1.0
 **/
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@BaseResponse
@RequestMapping("/menu")
public class MenuController {
    @Resource
    MenuService menuService;

    @GetMapping("/list")
    public List<Menu> list(){
        return menuService.list();
    }

    @PatchMapping("/edit")
    public boolean edit(@RequestBody Menu menu){
        return menuService.modify(menu);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Menu menu){
        return menuService.add(menu);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam long id){
        return menuService.delete(id);
    }
}
