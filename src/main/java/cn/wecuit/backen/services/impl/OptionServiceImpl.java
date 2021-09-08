package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.Option;
import cn.wecuit.backen.mapper.OptionMapper;
import cn.wecuit.backen.services.OptionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/21 18:56
 * @Version 1.0
 **/
@Service
public class OptionServiceImpl extends ServiceImpl<OptionMapper, Option> implements OptionService {

    @Override
    public List<Option> getByPrefix(String prefix) {
        return this.getBaseMapper().selectList(new QueryWrapper<Option>() {{
            likeRight("name", prefix);
        }});
    }

    @Override
    public Object getValueByName(String name) {
        Option option = this.getBaseMapper().selectOne(new QueryWrapper<Option>() {{
            eq("name", name);
            select("value");
        }});
        if(option == null)return null;
        return option.getValue();
    }

    @Override
    public boolean updateValueByName(Option option) {
        option.setId(null);
        int update = this.getBaseMapper().update(option, new UpdateWrapper<Option>() {{
            eq("name", option.getName());
        }});
        return update == 1;
    }

    @Override
    public boolean addNew(Option option) {
        option.setId(null);
        int insert = this.getBaseMapper().insert(option);
        return insert == 1;
    }
}
