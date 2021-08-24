package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.Media;
import cn.wecuit.backen.mapper.MediaMapper;
import cn.wecuit.backen.services.MediaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/24 20:24
 * @Version 1.0
 **/
@Service
public class MediaServiceImpl implements MediaService {
    @Resource
    MediaMapper mediaMapper;

    @Override
    public Map<String, Object> list(int page, int limit) {
        Page<Map<String, Object>> mapPage = mediaMapper.selectMapsPage(new Page<>(page, limit), null);

        return new HashMap<String, Object>(){{
            put("list", mapPage.getRecords());
            put("totalPages", mapPage.getTotal());
            put("current", mapPage.getCurrent());
        }};
    }
}
