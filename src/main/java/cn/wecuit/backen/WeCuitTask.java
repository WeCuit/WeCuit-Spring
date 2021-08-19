package cn.wecuit.backen;

import cn.wecuit.backen.services.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Scheduled(cron = "1 0/10 * * * ?")
    public void newsTask(){
        newsService.pullNews();
    }
}
