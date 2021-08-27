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
@TableName("wc_menus")
public class Menu {
    private Long id;                // 菜单ID
    private String name;            // 菜单名
    private String path;            // 菜单路径
    private String icon;            // 菜单图标
}
