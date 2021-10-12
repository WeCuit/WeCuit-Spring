package cn.wecuit.robot.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author jiyec
 * @Date 2021/10/8 21:35
 * @Version 1.0
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCmd {
    String keyword();           // 指令
    String desc() default "";              // 使用说明
    boolean regAsMainCmd() default false;    // 是否注册为一级指令
}
