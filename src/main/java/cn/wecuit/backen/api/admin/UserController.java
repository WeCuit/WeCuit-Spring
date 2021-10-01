package cn.wecuit.backen.api.admin;

import cn.dev33.satoken.stp.StpUtil;
import cn.wecuit.backen.pojo.AdminUser;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.AdminUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/28 16:29
 * @Version 1.0
 **/
@BaseResponse
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    AdminUserService adminUserService;

    @ApiOperation(value = "用户注册")
    @PostMapping("/admin/reg")
    public Map<String, Object> register(@RequestBody AdminUser user){
        user.setId(null);   // 清除可能自定义的ID
        boolean register = adminUserService.register(user);
        return new HashMap<String, Object>(){{
            put("result", register);
        }};
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("/admin/login")
    public Map<String, Object> login(@RequestBody AdminUser user){
        String[] login = adminUserService.login(user);
        return new HashMap<String, Object>(){{
            put("auth", login);
        }};
    }

    @ApiOperation(value = "用户注销")
    @GetMapping("/admin/logout")
    public Map<String, Object> logout(){
        StpUtil.logout();
        return new HashMap<String, Object>(){{
            put("result", true);
        }};
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/admin/info")
    public Map<String, Object> getAdminUserInfo(){
        Object loginId = StpUtil.getLoginId();
        AdminUser info = adminUserService.info(Long.parseLong((String) loginId));
        return new HashMap<String, Object>(){{
            put("info", info);
        }};
    }
    
    @ApiOperation(value = "修改用户信息")
    @PutMapping("/admin/info")
    public Map<String, Object> putAdminUserInfo(@RequestBody AdminUser user){
        Object loginId = StpUtil.getLoginId();
        long id = Long.parseLong((String) loginId);
        boolean b = adminUserService.modifyInfoById(id, user);
        return new HashMap<String, Object>(){{
            put("result", b);
        }};
    }

    @ApiOperation(value = "用户菜单")
    @GetMapping("/admin/routes")
    public Map<String, Object> userMenu(){
        List<String> list = adminUserService.userMenu(StpUtil.getLoginIdAsLong());
        return new HashMap<String, Object>(){{
            put("authedRoutes", list);
        }};
    }
}
