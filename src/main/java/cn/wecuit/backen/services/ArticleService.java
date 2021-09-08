package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.Article;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/7 8:57
 * @Version 1.0
 **/
public interface ArticleService {
    /**
     * 发布文章|向数据库插入文章
     * @param article 文章实体
     * @return 插入成功返回ID，插入失败返回-1
     */
    long publish(Article article);

    /**
     * 删除文章
     * @param id 文章ID
     * @return 成功true | 失败false
     */
    boolean delete(long id);

    /**
     * 获取指定文章详情
     * @param id 文章ID
     * @return 文章实体
     */
    Article detail(long id);

    /**
     * 编辑文章
     * @param article 文章实体
     * @return  成功true | 失败false
     */
    boolean edit(Article article);

    /**
     * 分页获取文章
     * @param page 页数
     * @param limit 每页文章数
     * @return  相关信息
     */
    Map<String, Object> list(int page, int limit);
}
