package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.bean.Article;
import cn.wecuit.backen.mapper.ArticleMapper;
import cn.wecuit.backen.services.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/7 8:57
 * @Version 1.0
 **/
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    ArticleMapper articleMapper;

    @Override
    public long publish(Article article) {
        article.setId(null);
        int insert = articleMapper.insert(article);
        if (insert == 1)
            return article.getId();
        else
            return -1;
    }

    @Override
    public boolean delete(long id) {
        int i = articleMapper.deleteById(id);
        return i == 1;
    }

    @Override
    public Article detail(long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public boolean edit(Article article) {
        int i = articleMapper.updateById(article);
        return i == 1;
    }

    @Override
    public Map<String, Object> list(int page, int limit) {
        Page<Article> articlePage = articleMapper.selectPage(new Page<>(page, limit), new QueryWrapper<Article>(){{
            select("id", "title", "created");
        }});
        return new HashMap<String, Object>(){{
            put("rows", articlePage.getRecords());
            put("total", articlePage.getTotal());
            put("pages", articlePage.getPages());
        }};
    }
}
