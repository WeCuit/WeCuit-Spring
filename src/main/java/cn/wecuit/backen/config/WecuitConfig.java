package cn.wecuit.backen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author jiyec
 * @Date 2021/8/5 9:21
 * @Version 1.0
 **/
@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "wecuit")
public class WecuitConfig {
    private String dataPath;
    private String localhost;
    private Robot robot;
    private AES aes;
    private RSA rsa;
    private OCR ocr;
    private WX wx;
    private QQ qq;

    @Data
    public static class Robot{
        private Long id;
        private String pass;
    }
    @Data
    public static class AES{
        private String key;
    }
    @Data
    public static class RSA{
        private String pri;
        private String pub;
    }
    @Data
    public static class OCR{
        private String server;
        private String salt;
    }
    @Data
    public static class WX{
        private String appid;
        private String secret;
    }
    @Data
    public static class QQ{
        private String appid;
        private String secret;
    }
}

