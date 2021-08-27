package cn.wecuit.backen.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jiyec
 * @Date 2021/8/27 15:49
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wc_permissions")
public class Permission {
    private Long id;                // 权限ID
    private String mark;            // 权限标记
    private String name;            // 权限名
}
