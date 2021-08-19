package cn.wecuit.backen.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author jiyec
 * @Date 2021/8/5 9:21
 * @Version 1.0
 **/
@Component
@EnableConfigurationProperties
@Data
@ConfigurationProperties(prefix = "wecuit")
public class WecuitConfig {
    private Robot robot;

    @Data
    public static class Robot{
        private Long id;
        private String pass;
    }
}

