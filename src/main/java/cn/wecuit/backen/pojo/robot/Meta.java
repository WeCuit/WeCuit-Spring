package cn.wecuit.backen.pojo.robot;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jiyec
 * @Date 2021/8/21 21:05
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rb_meta")
public class Meta {
    private Long id;
    private String name;
    private String value;
}
