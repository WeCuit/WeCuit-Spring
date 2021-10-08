package cn.wecuit.robot.services.impl;

import cn.wecuit.robot.mapper.MetaMapper;
import cn.wecuit.robot.pojo.Meta;
import cn.wecuit.robot.services.RbNewsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/10/8 11:37
 * @Version 1.0
 **/
@Service
public class RbNewsServiceImpl implements RbNewsService {
    @Resource
    MetaMapper metaMapper;

    @Override
    public boolean isNewsExist(String md5) {
        Integer c = metaMapper.selectCount(new QueryWrapper<Meta>() {{
            eq("name", "_transient_timeout-" + md5);
        }});
        return c != null && c > 0;
    }

    @Override
    public boolean addNoticed(String md5) {
        int insert = metaMapper.insert(new Meta(null, md5, String.valueOf(new Date().getTime())));
        return insert == 1;
    }

    @Override
    public int delOutDated() {
        return metaMapper.delete(new QueryWrapper<Meta>() {{
            lt("value", String.valueOf(new Date().getTime()));
        }});
    }
}
