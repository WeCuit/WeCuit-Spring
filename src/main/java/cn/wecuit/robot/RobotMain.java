package cn.wecuit.robot;

import cn.wecuit.robot.data.DataHandle;
import cn.wecuit.robot.provider.WSeg;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;

/**
 * @Author jiyec
 * @Date 2021/5/4 23:00
 * @Version 1.0
 **/
@Slf4j
public class RobotMain {
    @Getter
    private static Bot bot;

    public static void init(Long id, String pass, String path){
        init(id, pass, new BotConfiguration() {{
            // 配置，例如：
            fileBasedDeviceInfo(path + "/robot/device.json");
            setCacheDir(new File(path + "/robot/cache/" + id));
        }});
    }

    public static void init(Long id, String pass, BotConfiguration config){

        log.info("新建机器人实例");
        bot = BotFactory.INSTANCE.newBot(id, pass, config);
        log.info("开始登录");
        bot.login();

        afterLogin();
    }

    public static void logout(){
        if(null != bot)
            bot.close();
    }

    private static void afterLogin(){
        log.info("处理登录后操作");

        log.info("数据初始化");
        DataHandle.init(bot);

        log.info("插件初始化");
        MainHandleJava.init(bot);

        // 分词初始化
        // new InitWseg().start();
    }

}

@Slf4j
class InitWseg extends Thread{
    @Override
    public void run() {
        log.info("分词模型预加载....");
        WSeg.seg("笑死了");
        log.info("分词模型预加载完毕....");
    }
}