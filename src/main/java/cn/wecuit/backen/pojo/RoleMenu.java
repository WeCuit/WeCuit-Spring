package cn.wecuit.backen.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jiyec
 * @Date 2021/8/27 15:52
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wc_role_menu")
public class RoleMenu {
    private Long roleId;                // 角色ID
    private Long menuId;                // 菜单ID
    @TableField(exist = false)
    private Integer type;               // 1.增加 2.删除
}
