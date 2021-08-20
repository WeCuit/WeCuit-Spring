package cn.wecuit.backen.services;

import cn.wecuit.backen.bean.College;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/20 13:10
 * @Version 1.0
 **/
public interface CollegeService {
    /**
     * 获取学院列表
     *
     * @return
     */
    Map<String, Object> getList(int page, int limit);

    boolean add(College college);

    boolean delete(long id);

    boolean update(College college);
}
