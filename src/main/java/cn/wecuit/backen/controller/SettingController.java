package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.College;
import cn.wecuit.backen.bean.ResponseData;
import cn.wecuit.backen.services.CollegeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/20 11:20
 * @Version 1.0
 **/
@RestController
@RequestMapping("/settings")
public class SettingController {
    @Resource
    CollegeService collegeService;

    @ApiOperation(value = "学院列表", notes = "获取学院列表")
    @GetMapping("/college/list")
    public ResponseData getCollegeList(@RequestParam(required = false, defaultValue = "1") int page,
                                       @RequestParam(required = false, defaultValue = "10") int limit) {
        Map<String, Object> data = collegeService.getList(page, limit);
        return new ResponseData() {{
            setCode(200);
            setData(data);
        }};
    }

    @ApiOperation(value = "添加学院", notes = "添加新的学院")
    @PutMapping("/college/add")
    public ResponseData addCollege(@RequestBody College college) {
        boolean add = collegeService.add(college);

        return new ResponseData() {{
            setCode(add ? 200 : 201);
            setMsg(add ? "success" : "fail");
        }};
    }

    @ApiOperation(value = "删除学院", notes = "删除指定学院")
    @DeleteMapping("/college/delete")
    public ResponseData delCollege(@RequestBody College college) {
        boolean delete = collegeService.delete(college.getId());

        return new ResponseData() {{
            setCode(delete ? 200 : 201);
            setMsg(delete ? "success" : "fail");
        }};
    }

    @PatchMapping("/college/edit")
    public ResponseData editCollege(@RequestBody College college){
        boolean update = collegeService.update(college);

        return new ResponseData() {{
            setCode(update ? 200 : 201);
            setMsg(update ? "success" : "fail");
        }};
    }
}
