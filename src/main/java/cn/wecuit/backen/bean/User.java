package cn.wecuit.backen.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author jiyec
 * @Date 2021/8/21 20:46
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wc_users")
public class User implements Serializable {
    private Long id;                    // 系统用户ID
    private String stuId;               // 学号
    private String stuPass;             // 学生密码
    private String wxId;                // 微信ID
    private String qqId;                // QQ ID
}
