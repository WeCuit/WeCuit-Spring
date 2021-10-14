package cn.wecuit.robot.services;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/10/8 10:59
 * @Version 1.0
 **/
public interface RbDictService {
    boolean add(String keyword, String value);
    List<String> getByKeyword(List<String> keys);
}
