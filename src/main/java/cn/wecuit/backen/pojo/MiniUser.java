package cn.wecuit.backen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("wc_miniusers")
public class MiniUser implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;                    // 系统用户ID
    private String stuId;               // 学号
    private String stuPass;             // 学生密码
    private String wxId;                // 微信ID
    private String qqId;                // QQ ID
}
