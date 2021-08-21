package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.Option;

/**
 * @Author jiyec
 * @Date 2021/8/21 18:56
 * @Version 1.0
 **/
public interface OptionService {
    Object getValueByName(String name);
    boolean updateValueByName(Option option);
}
