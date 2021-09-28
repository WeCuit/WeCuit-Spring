package cn.wecuit.backen.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.wecuit.backen.bean.*;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.*;
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

@BaseResponse
@Api(value = "授权管理", tags = {"授权操作接口"})
@ApiOperation(value = "授权管理")
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@RequestMapping("/auth")
public class AdminAuthController {
    @Resource
    MenuService menuService;
    @Resource
    RoleService roleService;
    @Autowired
    RoleMenuService roleMenuService;
    @Resource
    AdminAuthService adminAuthService;

    @ApiOperation(value = "用户注册")
    @PostMapping("/user/reg")
    public Map<String, Object> register(@RequestBody AdminUser user){
        user.setId(null);   // 清除可能自定义的ID
        boolean register = adminAuthService.register(user);
        return new HashMap<String, Object>(){{
            put("result", register);
        }};
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("/user/login")
    public Map<String, Object> login(@RequestBody AdminUser user){
        String[] login = adminAuthService.login(user);
        return new HashMap<String, Object>(){{
            put("auth", login);
        }};
    }

    @ApiOperation(value = "用户注销")
    @GetMapping("/user/logout")
    public Map<String, Object> logout(){
        StpUtil.logout();
        return new HashMap<String, Object>(){{
            put("result", true);
        }};
    }

    @ApiOperation(value = "用户信息")
    @GetMapping("/user/info")
    public Map<String, Object> userInfo(){
        boolean b = StpUtil.hasRole("123");
        return new HashMap<String, Object>(){{
            put("result", b);
        }};
    }

    @ApiOperation(value = "用户菜单")
    @GetMapping("/user/routes")
    public Map<String, Object> userMenu(){

        List<String> list = adminAuthService.userMenu(StpUtil.getLoginIdAsLong());
        return new HashMap<String, Object>(){{
            put("authedRoutes", list);
        }};
    }


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

    @ApiOperation(value = "获取角色拥有的菜单")
    @GetMapping("/roles/menus")
    public Map<String, Object> listRoleMenu(@RequestParam long role_id){
        List<Long> list = roleMenuService.selectRoleMenus(role_id);
        return new HashMap<String, Object>(){{
            put("authedRoutes", list);
        }};
    }

    @PatchMapping("/roles/menus")
    public Map<String, Object> modifyRoleMenu(@RequestBody List<RoleMenu> list){
        boolean add = true, delete = true;
        List<RoleMenu> addList = list.stream().filter(e -> e.getType() == 1).collect(Collectors.toList());
        List<RoleMenu> deleteList = list.stream().filter(e -> e.getType() == 2).collect(Collectors.toList());
        if(addList.size() > 0)add = roleMenuService.saveBatch(addList);
        if(deleteList.size() > 0)delete = roleMenuService.removeList(deleteList);
        boolean finalAdd = add;
        boolean finalDelete = delete;
        return new HashMap<String, Object>(){{
            put("add", finalAdd);
            put("delete", finalDelete);
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
