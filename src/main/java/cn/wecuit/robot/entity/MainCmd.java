package cn.wecuit.robot.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author jiyec
 * @Date 2021/10/8 21:07
 * @Version 1.0
 * @description 一级主指令
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MainCmd {
    String keyword();   // 指令
    String desc();      // 描述
}
