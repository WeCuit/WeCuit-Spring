package cn.wecuit.backen.utils.HTTP;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jiyec
 * @Date 2021/11/2 16:54
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequestConfig {
    private int maxRedirects;
}
