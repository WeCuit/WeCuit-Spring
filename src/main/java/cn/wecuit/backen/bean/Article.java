package cn.wecuit.backen.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/9/7 8:48
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wc_posts")
public class Article {
    @ApiModelProperty("文章ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    @ApiModelProperty("文章作者")
    private Long author;
    @ApiModelProperty("文章标题")
    private String title;
    @ApiModelProperty("文章内容")
    private String content;
    @ApiModelProperty("发布时间")
    private Date created;
    @ApiModelProperty("文章类型")
    private String type;
}
