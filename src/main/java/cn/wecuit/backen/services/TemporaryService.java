package cn.wecuit.backen.services;

import cn.wecuit.backen.pojo.Temporary;

import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/9/30 18:40
 * @Version 1.0
 **/
public interface TemporaryService {
    boolean addNew(Temporary temporary);
    Temporary getByName(String name);
    boolean addNew(String name, String value, Date time);

    boolean deleteByName(String name);

    boolean updateByName(Temporary temporary);

    int deleteOutDate();
}
