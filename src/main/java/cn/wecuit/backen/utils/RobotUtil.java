package cn.wecuit.backen.utils;

import cn.wecuit.robot.RobotMain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 机器人启动关闭控制
 *
 * @Author jiyec
 * @Date 2021/5/5 9:45
 * @Version 1.0
 **/
@Slf4j
@Component
public class RobotUtil {

    public static Long id;
    private static String pass;

    @Value("${wecuit.robot.id:0}")
    public void setId(Long id) {
        RobotUtil.id = id;
    }

    @Value("${wecuit.robot.pass:}")
    public void setPass(String pass) {
        RobotUtil.pass = pass;
    }

    public static void start(){
        if(id != 0L && !"".equals(pass))
            RobotMain.init(id, pass);
        else
            log.info("机器人账号数据似乎未初始化，无法启动机器人！");
    }

    public static void stop(){
        RobotMain.logout();
    }

}