package cn.wecuit.backen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author jiyec
 * @Date 2021/8/20 13:03
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wc_colleges")
public class College implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("学院名称")
    private String name;
    @ApiModelProperty("学院标记")
    private String mark;
}
