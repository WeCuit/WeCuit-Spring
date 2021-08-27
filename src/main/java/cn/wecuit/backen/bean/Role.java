package cn.wecuit.backen.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("wc_roles")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;                // 角色ID
    private String mark;            // 角色标记
    private String name;            // 角色名
}
