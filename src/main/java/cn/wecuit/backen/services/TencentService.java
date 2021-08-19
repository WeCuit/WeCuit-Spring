package cn.wecuit.backen.services;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/17 20:08
 * @Version 1.0
 **/
public interface TencentService {
    Map<String, Object> code2session(String code, int client);
}
