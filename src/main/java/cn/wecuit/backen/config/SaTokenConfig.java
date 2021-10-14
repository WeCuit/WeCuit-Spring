package cn.wecuit.backen.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * @Author jiyec
 * @Date 2021/9/6 9:06
 * @Version 1.0
 **/
@Configuration
@Slf4j
public class SaTokenConfig implements WebMvcConfigurer {
    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaRouteInterceptor((req, res, handler) -> {
            // TODO:
            log.info("路由拦截 - {}", StpUtil.isLogin());

            // 根据路由划分模块，不同模块不同鉴权
            // SaRouter.match("/user/**", () -> StpUtil.checkPermission("user"));
            SaRouter.match(Collections.singletonList("/admin/**"), Arrays.asList("/admin/user/login", "/admin/user/reg", "/admin/auth/mini/**", "/admin/test/123"), ()->{
                StpUtil.checkLogin();   // 检测登录
                StpUtil.checkPermission("login");   //检测ADMIN登录
            });

             //SaRouter.match("/v3/**", "/v3/auth/getAccessToken", () -> StpMiniUtil.checkPermission("mini"));
            // SaRouter.match("/notice/**", () -> StpUtil.checkPermission("notice"));
            // SaRouter.match("/comment/**", () -> StpUtil.checkPermission("comment"));
        })).addPathPatterns("/**");
    }
}
