package cn.wecuit.backen;

import cn.wecuit.backen.services.NewsService;
import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.data.NewsStorage;
import cn.wecuit.robot.plugins.msg.NewsPlugin;
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
}
