package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.Media;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/24 20:24
 * @Version 1.0
 **/
public interface MediaService {
    boolean store(Media media);
    Map<String, Object> list(int page, int limit);
}
