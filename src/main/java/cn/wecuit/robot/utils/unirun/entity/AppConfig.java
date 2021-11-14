package cn.wecuit.robot.utils.unirun.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jiyec
 * @Date 2021/10/17 13:39
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppConfig {
    String appVersion;
    String brand;
    String deviceToken = "";
    String deviceType = "1";
    String mobileType;
    String sysVersion;
}
