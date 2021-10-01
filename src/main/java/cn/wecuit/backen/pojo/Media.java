package cn.wecuit.backen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author jiyec
 * @Date 2021/8/24 20:21
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wc_media")
public class Media implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;                // 媒体ID
    private String author;          // 上传者
    private String path;            // 媒体路径
    private String mime;            // 媒体类型
}
