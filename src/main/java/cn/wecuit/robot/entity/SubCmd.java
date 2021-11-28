package cn.wecuit.robot.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 二级指令处理
 *
 * @Author jiyec
 * @Date 2021/10/8 21:35
 * @Version 1.0
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCmd {
    /**
     * 空字符串或默认为全局监听事件
     **/
    String keyword() default "";
    /**
     * 指令使用说明、介绍
     **/
    String desc() default "";
    /**
     * 是否注册为一级指令
     **/
    boolean regAsMainCmd() default false;

    /**
     * 是否需要管理员权限
     */
    boolean requireAdmin() default false;
}
