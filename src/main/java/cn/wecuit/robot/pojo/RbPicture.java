package cn.wecuit.robot.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/8 12:01
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(autoResultMap = true)
public class RbPicture {
    private Long id;
    private String imgId;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> info;
    private String level;
}
