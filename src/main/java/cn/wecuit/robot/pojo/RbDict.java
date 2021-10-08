package cn.wecuit.robot.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jiyec
 * @Date 2021/10/8 10:55
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RbDict {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keyword;
    private String value;
}
