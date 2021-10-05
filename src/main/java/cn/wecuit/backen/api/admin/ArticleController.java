package cn.wecuit.backen.api.admin;

import cn.wecuit.backen.pojo.Article;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/7 8:45
 * @Version 1.0
 **/
@Api("文章模块")
@RequestMapping("/article")
@RestController
@BaseResponse
public class ArticleController {
    @Resource
    ArticleService articleService;

    @ApiOperation("获取文章列表")
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int limit){
        return articleService.list(page, limit);
    }

    @ApiOperation("发布文章")
    @PostMapping("/publish")  
    public Map<String, Object> publish(@RequestBody Article article){
        long publish = articleService.publish(article);
        if(publish == -1)throw new BaseException(301, "文章发布失败");
        return new HashMap<String, Object>(){{
            put("id", publish);
        }};
    }

    @ApiOperation("获取文章详情")
    @GetMapping("/detail/{id}")
    public Article detail(@PathVariable long id){
        Article article = articleService.detail(id);
        if(article==null)throw new BaseException(404, "文章不存在");
        return article;
    }

    @ApiOperation("修改文章内容")
    @PutMapping("/edit/{id}")
    public Map<String, Object> edit(@PathVariable long id, @RequestBody Article article){
        article.setId(id);
        boolean edit = articleService.edit(article);
        return new HashMap<String, Object>(){{
            put("result", edit);
        }};
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable long id){
        boolean delete = articleService.delete(id);
        return new HashMap<String, Object>(){{
            put("result", delete);
        }};
    }
}
