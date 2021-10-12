package cn.wecuit.robot.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/8 11:36
 * @Version 1.0
 **/
public interface RbNewsService {
    boolean isNewsExist(String md5);
    boolean addNoticed(String md5);
    int delOutDated();
    List<Map<String, String>> getLatestNews(int dayRange) throws IOException;
}
