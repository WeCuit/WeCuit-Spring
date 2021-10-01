package cn.wecuit.backen.pojo.robot;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("rb_plugin")
public class PluginData implements Serializable {
    private Long id;                                // ID
    private String name;                            // 插件名
    private Map<String, Object> config;             // 插件配置
}
