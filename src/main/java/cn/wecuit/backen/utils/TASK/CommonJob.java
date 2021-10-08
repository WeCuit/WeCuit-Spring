package cn.wecuit.backen.utils.TASK;

import cn.wecuit.backen.services.NewsService;
import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.data.NewsStorage;
import cn.wecuit.robot.plugins.msg.NewsPlugin;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/5/11 19:44
 * @Version 1.0
 **/
@Slf4j
public class CommonJob implements Job {
    @Resource
    NewsService newsService;

    SimpleDateFormat minuteSDF = new SimpleDateFormat("mm");
    SimpleDateFormat hourSDF = new SimpleDateFormat("HH");


    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("执行任务");
        log.info("上一次执行时间：{}", jobExecutionContext.getPreviousFireTime());

        if(jobExecutionContext.getPreviousFireTime() == null){
            // 第一次执行
        }

        // 普通任务内容
        Date date = new Date();
        int minute = Integer.parseInt(minuteSDF.format(date));
        int hour = Integer.parseInt(hourSDF.format(date));

        if(minute % 9 == 0)
            newsService.pullNews();

        if(minute % 9 == 0) {
            //if(RobotUtil.id != 0L)
            //    newsService.newsNotice(NewsPlugin.getEnabledList());
            if(RobotMain.getBot() != null){
                newsService.newsNotice(NewsPlugin.getEnabledList());
            }
            // PixivTask.pullTask();
        }

        if(hour == 0 && minute ==0)
            // 清空过期的新闻数据
            NewsStorage.delOutDate();

    }
}
