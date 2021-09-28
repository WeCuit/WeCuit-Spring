package cn.wecuit.backen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling       // 启用计划任务
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WeCuitSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeCuitSpringApplication.class, args);
    }

}
