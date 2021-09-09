package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.Media;
import cn.wecuit.backen.mapper.MediaMapper;
import cn.wecuit.backen.services.MediaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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

    @Value("${wecuit.data-path}")
    private String BASE_STORE_PATH;

    @Override
    public boolean store(Media media) {
        media.setId(null);
        int insert = mediaMapper.insert(media);
        return insert == 1;
    }

    @Override
    public Map<String, Object> list(int page, int limit) {
        Page<Map<String, Object>> mapPage = mediaMapper.selectMapsPage(new Page<>(page, limit), null);

        return new HashMap<String, Object>(){{
            put("list", mapPage.getRecords());
            put("totalPages", mapPage.getTotal());
            put("current", mapPage.getCurrent());
        }};
    }

    @Override
    public boolean delete(long id) {
        Media media = mediaMapper.selectById(id);
        if(media == null)return true;
        int i = mediaMapper.deleteById(id);
        if(i == 1)new File(BASE_STORE_PATH + media.getPath()).delete();
        return i == 1;
    }


}
