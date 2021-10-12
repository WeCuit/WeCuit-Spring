package cn.wecuit.robot.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记类为机器人插件
 *
 * @Author jiyec
 * @Date 2021/10/10 17:36
 * @Version 1.0
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RobotPlugin {
}
