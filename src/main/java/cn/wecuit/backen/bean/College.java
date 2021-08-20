package cn.wecuit.backen.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

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
    private String name;
    private String mark;
}
