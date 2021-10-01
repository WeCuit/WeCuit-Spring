package cn.wecuit.backen.services;

import cn.wecuit.backen.pojo.Option;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/21 18:56
 * @Version 1.0
 **/
public interface OptionService extends IService<Option> {
    List<Option> getByPrefix(String prefix);
    Object getValueByName(String name);
    boolean updateValueByName(Option option);
    boolean addNew(Option option);
}
