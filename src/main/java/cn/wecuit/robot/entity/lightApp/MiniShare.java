package cn.wecuit.robot.entity.lightApp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/10/27 16:42
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
public class MiniShare {
    private String app;
    private String view;
    private String version;
    private String prompt;
    private Meta meta;
    private Config config;
    @Data
    public static class Meta{

    }
    @Data
    public static class Config{
        String type;
        int width = 0;
        int height = 0;
        int forward = 0;
        int autoSize = 0;
        long ctime = new Date().getTime();
        String token = "d40add3d97e942354560875862dcccdc";
    }
}
