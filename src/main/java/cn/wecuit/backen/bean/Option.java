package cn.wecuit.backen.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author jiyec
 * @Date 2021/8/21 18:28
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wc_options", autoResultMap = true)
public class Option implements Serializable {
    private Long id;
    private String name;            // 选项名
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object value;           // 选项值
}
