package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.pojo.College;
import cn.wecuit.backen.mapper.CollegeMapper;
import cn.wecuit.backen.services.CollegeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/20 13:10
 * @Version 1.0
 **/
@Service
public class CollegeServiceImpl implements CollegeService {
    @Resource
    CollegeMapper collegeMapper;

    @Override
    public Map<String, Object> getList(int page, int limit) {
        Page<College> collegePage = collegeMapper.selectPage(new Page<College>(page, limit), null);

        return new HashMap<String, Object>(){{
            put("list", collegePage.getRecords());
            put("totalPages", collegePage.getTotal());
            put("current", collegePage.getCurrent());
            put("size", collegePage.getSize());
        }};
    }

    @Override
    public boolean add(College college) {
        college.setId(null);
        int insert = collegeMapper.insert(college);

        return insert == 1;
    }

    @Override
    public boolean delete(long id) {
        int i = collegeMapper.deleteById(id);
        return i == 1;
    }

    @Override
    public boolean update(College college) {
        int i = collegeMapper.updateById(college);
        return i==1;
    }
}
