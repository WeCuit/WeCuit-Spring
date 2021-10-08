package cn.wecuit.robot.services.impl;

import cn.wecuit.robot.mapper.MetaMapper;
import cn.wecuit.robot.mapper.RbPicMapper;
import cn.wecuit.robot.pojo.Meta;
import cn.wecuit.robot.pojo.RbPicture;
import cn.wecuit.robot.services.RbPictureService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/8 12:03
 * @Version 1.0
 **/
@Service
public class RbPictureServiceImpl implements RbPictureService {
    @Resource
    RbPicMapper rbPicMapper;
    @Resource
    MetaMapper metaMapper;

    @Override
    public boolean add(String imgId, Map<String, Object> info, String level) {
        int insert = rbPicMapper.insert(new RbPicture(null, imgId, info, level));
        return insert == 1;
    }

    @Override
    public boolean increCntByLevel(int level) {
        int update = metaMapper.update(new Meta(), new UpdateWrapper<Meta>() {{
            eq("name", "picture_cnt_" + level);
            setSql("value=meta_value+1");
        }});
        if(update == 0){
            update = metaMapper.insert(new Meta(null, "picture_cnt_" + level, "1"));
        }
        return update == 1;
    }
}
