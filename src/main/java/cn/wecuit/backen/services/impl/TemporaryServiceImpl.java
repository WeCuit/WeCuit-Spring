package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.mapper.TemporaryMapper;
import cn.wecuit.backen.pojo.Temporary;
import cn.wecuit.backen.services.TemporaryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/9/30 18:40
 * @Version 1.0
 **/
@Service
public class TemporaryServiceImpl implements TemporaryService {
    @Resource
    TemporaryMapper temporaryMapper;

    @Override
    public boolean addNew(Temporary temporary) {
        int insert = temporaryMapper.insert(temporary);
        return insert == 1;
    }

    @Override
    public Temporary getByName(String name) {
        return temporaryMapper.selectOne(new QueryWrapper<Temporary>() {{
            eq("name", name);
        }});
    }

    @Override
    public boolean addNew(String name, String value, Date time) {
        return addNew(new Temporary() {{
            setName(name);
            setValue(value);
            setTime(time);
        }});
    }

    @Override
    public boolean deleteByName(String name) {
        int delete = temporaryMapper.delete(new QueryWrapper<Temporary>() {{
            eq("name", name);
        }});
        return delete == 1;
    }

    @Override
    public boolean updateByName(Temporary temporary) {
        int name = temporaryMapper.update(temporary, new UpdateWrapper<Temporary>() {{
            eq("name", temporary.getName());
        }});
        return name == 1;
    }

    @Override
    public int deleteOutDate() {
        return temporaryMapper.delete(new QueryWrapper<Temporary>() {{
            le("time", new Date());
        }});
    }
}
