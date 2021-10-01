package cn.wecuit.backen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ApiModelProperty(name = "角色名")
    private String name;
    @ApiModelProperty(name = "角色备注")
    private String remark;
    @ApiModelProperty(name = "角色状态")
    private Integer state;
}
