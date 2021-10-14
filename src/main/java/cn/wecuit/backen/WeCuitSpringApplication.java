package cn.wecuit.backen;

import cn.wecuit.backen.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling       // 启用计划任务
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Import(SpringUtil.class)
@ComponentScan({"cn.wecuit.backen", "cn.wecuit.robot"})
@MapperScan({"cn.wecuit.robot.mapper", "cn.wecuit.backen.mapper"})
public class WeCuitSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeCuitSpringApplication.class, args);
    }

}
