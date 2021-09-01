package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.Menu;
import cn.wecuit.backen.bean.Role;
import cn.wecuit.backen.bean.RoleMenu;
import cn.wecuit.backen.services.MenuService;
import cn.wecuit.backen.services.RoleMenuService;
import cn.wecuit.backen.services.RoleService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author jiyec
 * @Date 2021/8/28 6:18
 * @Version 1.0
 **/

@Api(value = "授权管理", tags = {"授权操作接口"})
@ApiOperation(value = "授权管理")
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    MenuService menuService;

    @Resource
    RoleService roleService;

    @Autowired
    RoleMenuService roleMenuService;

    @ApiOperation(value = "获取角色列表")
    @GetMapping("/roles")
    public List<Role> listRole(){
        return roleService.list();
    }
    @ApiOperation(value = "添加角色")
    @PostMapping("/roles")
    public Map<String, Object> addRole(@RequestBody Role role){
        boolean add = roleService.add(role);
        return new HashMap<String,Object>(){{
            put("result", add);
        }};
    }
    @ApiOperation(value = "删除角色")
    @DeleteMapping("/roles/{id}")
    public Map<String, Object> deleteRole(@PathVariable long id){
        boolean delete = roleService.delete(id);
        return new HashMap<String,Object>(){{
            put("result", delete);
        }};
    }
    @ApiOperation(value = "修改角色信息")
    @PatchMapping("/roles/{id}")
    public Map<String, Object> modifyRole(@PathVariable long id, @RequestBody Role role){
        role.setId(id);
        boolean modify =  roleService.modify(role);
        return new HashMap<String,Object>(){{
            put("result", modify);
        }};
    }

    @PatchMapping("/roles/menus")
    public Map<String, Object> modifyRoleMenu(@RequestBody List<RoleMenu> list){
        boolean add = roleMenuService.saveBatch(list.stream().filter(e -> e.getType() == 1).collect(Collectors.toList()));
        boolean delete = roleMenuService.removeList(list.stream().filter(e -> e.getType() == 2).collect(Collectors.toList()));

        return new HashMap<String, Object>(){{
            put("add", add);
            put("delete", delete);
        }};
    }

    @GetMapping("/menus")
    public List<Menu> listMenu(){
        return menuService.list();
    }

    @PatchMapping("/menus/{id}")
    public Map<String, Object> editMenu(@PathVariable long id, @RequestBody Menu menu){
        menu.setId(id);
        boolean modify = menuService.modify(menu);
        return new HashMap<String,Object>(){{
            put("result", modify);
        }};
    }

    @PostMapping("/menus")
    public Map<String, Object> addMenu(@RequestBody Menu menu){

        boolean add = menuService.add(menu);
        return new HashMap<String,Object>(){{
            put("result", add);
        }};
    }

    @DeleteMapping("/menus/{id}")
    public Map<String, Object> deleteMenu(@PathVariable long id){
        boolean delete = menuService.delete(id);
        return new HashMap<String,Object>(){{
            put("result", delete);
        }};
    }
}
