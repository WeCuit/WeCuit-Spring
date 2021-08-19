package cn.wecuit.backen.services;

import cn.wecuit.backen.entity.News;

import java.io.IOException;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/8/18 9:47
 * @Version 1.0
 **/
public interface NewsService {
    void pullNews(String dir, News news);

    void pullNews();

    void newsNotice(List<String> noticeList) throws IOException;
}
