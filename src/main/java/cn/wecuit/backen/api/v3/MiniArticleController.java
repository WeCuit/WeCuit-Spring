package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.pojo.Article;
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
public class MiniArticleController {
    @Resource
    ArticleService articleService;

    @ApiOperation("获取文章详情1")
    @GetMapping("/detail/{id}")
    public Article detail(@PathVariable long id){
        Article article = articleService.detail(id);
        if(article==null)throw new BaseException(404, "文章不存在");
        return article;
    }

    @ApiOperation("获取文章详情2")
    @GetMapping("/view/{id}")
    @ResponseBody
    public String view(@PathVariable long id){
        Article article = articleService.detail(id);
        if(article==null)return "文章不存在";
        return article.getContent();
    }


}
