package cn.wecuit.robot.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @Author jiyec
 * @Date 2021/10/8 21:35
 * @Version 1.0
 **/
@Target(ElementType.METHOD)
public @interface SubCmd {
    String keyword();           // 指令
    boolean regAsFirstCmd();    // 是否注册为一级指令
}
