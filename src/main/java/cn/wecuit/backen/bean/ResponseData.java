package cn.wecuit.backen.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应体实体
 *
 * @Author jiyec
 * @Date 2021/7/29 11:15
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
    private Integer code;       // 响应状态码
    private String msg;         // 响应信息
    private Object data;        // 响应数据
}
