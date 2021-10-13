package cn.wecuit.robot.services.impl;

import cn.wecuit.backen.utils.NewsUtil;
import cn.wecuit.robot.mapper.MetaMapper;
import cn.wecuit.robot.pojo.Meta;
import cn.wecuit.robot.services.RbNewsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/8 11:37
 * @Version 1.0
 **/
@Service
@Slf4j
public class RbNewsServiceImpl implements RbNewsService {
    @Resource
    MetaMapper metaMapper;
    @Value("${wecuit.data-path}")
    private String BASE_DATA_PATH;

    @Override
    public boolean isNewsExist(String md5) {
        Integer c = metaMapper.selectCount(new QueryWrapper<Meta>() {{
            eq("name", "_transient_timeout-" + md5);
        }});
        return c != null && c > 0;
    }

    @Override
    public boolean addNoticed(String md5) {
        int insert = metaMapper.insert(new Meta(null, "_transient_timeout-" + md5, String.valueOf(System.currentTimeMillis())));
        return insert == 1;
    }

    @Override
    public int delOutDated() {
        return metaMapper.delNoticedNewsBeforeToday();
    }

    /**
     * 获取每个学院最新的新闻
     * @param dayRange  最近 [dayRange] 天的新闻
     * @return
     * @throws IOException
     */
    public List<Map<String, String>> getLatestNews(int dayRange) throws IOException {
        String listPath = BASE_DATA_PATH + "/WeCuit/cache/news/list";
        return NewsUtil.getLatestNews(listPath, dayRange);
    }
}
