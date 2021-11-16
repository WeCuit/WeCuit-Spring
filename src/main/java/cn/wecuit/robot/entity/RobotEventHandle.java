package cn.wecuit.robot.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author jiyec
 * @Date 2021/10/10 18:45
 * @Version 1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RobotEventHandle {
    //@AliasFor(annotation = RobotPlugin.class)
    EventType[] event();
}
