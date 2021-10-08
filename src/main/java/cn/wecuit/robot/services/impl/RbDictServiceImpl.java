package cn.wecuit.robot.services.impl;

import cn.wecuit.robot.mapper.DictMapper;
import cn.wecuit.robot.pojo.RbDict;
import cn.wecuit.robot.services.RbDictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/10/8 11:00
 * @Version 1.0
 **/
@Service
public class RbDictServiceImpl implements RbDictService {
    @Resource
    DictMapper dictMapper;

    @Override
    public boolean add(String keyword, String value) {
        int insert = dictMapper.insert(new RbDict() {{
            setKeyword(keyword);
            setValue(value);
        }});
        return insert == 1;
    }

    @Override
    public List<String> getByKeyword(List<String> keys) {
        return dictMapper.getMsgList2(keys);
    }
}
