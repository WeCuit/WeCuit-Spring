package cn.wecuit.backen.api.admin;

import cn.dev33.satoken.stp.StpUtil;
import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.AuthService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/30 18:00
 * @Version 1.0
 **/
@RestController
@RequestMapping("/auth")
@BaseResponse
public class MiniLoginController {

    @Resource
    AuthService authService;

    @ApiOperation("获取登录用小程序码")
    @GetMapping("/mini/qrcode/{type}")
    public Map<String, Object> getMiniCode(@PathVariable MiniType type){
        String id = "";
        if(StpUtil.isLogin())
            id = StpUtil.getLoginIdAsString();
        return authService.genQRCode(type, id, "ADMIN");
    }

    @ApiOperation("检测小程序登录授权结果")
    @GetMapping("/mini/login/check")
    public Map<String, Object> checkMiniLoginCode(@RequestParam String token){
        int i = authService.checkToken(token);
        Map<String, Object> result = new HashMap<>();
        result.put("result", i);
        return result;
    }

    @ApiOperation("管理端小程序登录操作")
    @GetMapping("/mini/binding")
    public Map<String, Object> adminMiniBinding(@RequestParam String token){
        long adminId = StpUtil.getLoginIdAsLong();
        boolean b = authService.bindAdminByToken(token, adminId);
        return new HashMap<String, Object>(){{
            put("result", b);
        }};
    }

    @ApiOperation("管理端小程序登录操作")
    @GetMapping("/mini/login")
    public Map<String, Object> adminMiniLogin(
            @RequestParam String token){
        // token -> openid -> adminUser
        String[] tokenInfo = authService.adminLoginByLoginToken(token);
        return new HashMap<String, Object>(){{
            put("auth", tokenInfo);
        }};
    }

}
