package cn.wecuit.backen.utils;

import cn.wecuit.backen.utils.TASK.CommonJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/5/11 17:45
 * @Version 1.0
 **/
public class TaskUtil {

    private static Scheduler scheduler;

    public static void start() throws SchedulerException {
        // 1、创建调度器Scheduler
        scheduler = new StdSchedulerFactory().getScheduler();

        // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
        JobDetail jobDetail = JobBuilder.newJob(CommonJob.class)
                .withIdentity("job1", "group1").build();

        // 3、构建Trigger实例,每隔1s执行一次
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                // .startNow()                                          // 立即生效
                .startAt(new Date(System.currentTimeMillis() + 5000))   // 5秒后开始计划任务
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(1)                       // 每隔1分钟执行一次
                        .repeatForever()                                // 一直执行
                ).build();

        //4、执行
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }

    public static void stop() throws SchedulerException {
        if(scheduler != null) scheduler.shutdown();
    }

}
