package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.bean.College;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.CollegeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/20 11:20
 * @Version 1.0
 **/
@BaseResponse
@RestController
@RequestMapping("/settings")
public class SettingController {
    @Resource
    CollegeService collegeService;

    @ApiOperation(value = "学院列表", notes = "获取学院列表")
    @GetMapping("/college/list")
    public Map<String, Object> getCollegeList(@RequestParam(required = false, defaultValue = "1") int page,
                                         @RequestParam(required = false, defaultValue = "10") int limit) {
        Map<String, Object> data = collegeService.getList(page, limit);
        return data;
    }

    @ApiOperation(value = "添加学院", notes = "添加新的学院")
    @PostMapping("/college/add")
    public Map<String, Object> addCollege(@RequestBody College college) {
        boolean add = collegeService.add(college);

        return new HashMap<String, Object>(){{
            put("result", add);
        }};
    }

    @ApiOperation(value = "删除学院", notes = "删除指定学院")
    @DeleteMapping("/college/delete/{id}")
    public Map<String, Object> delCollege(@PathVariable long id) {
        boolean delete = collegeService.delete(id);

        return new HashMap<String, Object>(){{
            put("result", delete);
        }};
    }

    @PatchMapping("/college/edit")
    public Map<String, Object> editCollege(@RequestBody College college){
        boolean update = collegeService.update(college);

        return new HashMap<String, Object>(){{
            put("result", update);
        }};
    }
}
