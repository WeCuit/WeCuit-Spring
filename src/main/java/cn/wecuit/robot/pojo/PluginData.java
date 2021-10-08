package cn.wecuit.robot.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/21 20:53
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "rb_plugin", autoResultMap = true)
public class PluginData implements Serializable {
    private Long id;                                // ID
    private String name;                            // 插件名

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> config;             // 插件配置
}
