package cn.wecuit.backen.services;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/24 20:24
 * @Version 1.0
 **/
public interface MediaService {
    Map<String, Object> list(int page, int limit);
}
