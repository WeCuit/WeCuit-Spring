package cn.wecuit.backen.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/27 15:48
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "wc_roles", autoResultMap = true)
public class Role {
    @ApiModelProperty(value = "角色ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    @ApiModelProperty(name = "角色标记")
    private String mark;
    @ApiModelProperty(name = "角色名")
    private String name;
}
