package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.Option;
import cn.wecuit.backen.mapper.OptionMapper;
import cn.wecuit.backen.services.OptionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author jiyec
 * @Date 2021/8/21 18:56
 * @Version 1.0
 **/
@Service
public class OptionServiceImpl implements OptionService {
    @Resource
    OptionMapper optionMapper;

    @Override
    public Object getValueByName(String name) {
        Option option = optionMapper.selectOne(new QueryWrapper<Option>() {{
            eq("name", name);
            select("value");
        }});
        return option.getValue();
    }

    @Override
    public boolean updateValueByName(Option option) {
        int update = optionMapper.update(option, new UpdateWrapper<Option>() {{
            eq("name", option.getName());
        }});
        return update == 1;
    }
}
