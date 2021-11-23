package cn.wecuit.backen;

import cn.wecuit.backen.services.NewsService;
import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.data.NewsStorage;
import cn.wecuit.robot.plugins.msg.NewsPlugin;
import cn.wecuit.robot.plugins.msg.UniRunPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author jiyec
 * @Date 2021/8/18 11:46
 * @Version 1.0
 **/
@Component
@Slf4j
public class WeCuitTask {
    @Resource
    NewsService newsService;

    // 从第0分钟开始每隔10分钟执行一次
    @Scheduled(cron = "1 0/10 * * * ?")
    public void newsPullTask(){
        newsService.pullNews();
    }

    // 从第5分钟开始每隔10分钟执行一次
    @Scheduled(cron = "0 5/10 * * * ?")
    public void newsNoticeTask(){
        NewsStorage.delOutDate();
        if(RobotMain.getBot() != null){
            try {
                newsService.newsNotice(NewsPlugin.getEnabledList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 周二到周五，每天7点从第20分钟开始每隔2分钟执行一次直到30分钟
    //@Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 20-30/2 7 ? * 2,3,4,5")
    public void UNIRUN_clubAutoJoin(){
        log.info("clubAutoJoin");
        UniRunPlugin.clubAutoJoin();
    }

    // 星期一到星期四
    //@Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 0,5,45,50,55 18 ? * 1,2,3,4")
    public void UNIRUN_signInOrSignBack1(){
        log.info("signInOrSignBack1");
        UniRunPlugin.signInOrSignBack();
    }

    // 星期一到星期四
    @Scheduled(cron = "0 50,55 17 ? * 1,2,3,4")
    public void UNIRUN_signInOrSignBack2(){
        log.info("signInOrSignBack2");
        UniRunPlugin.signInOrSignBack();
    }
}
