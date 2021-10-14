package cn.wecuit.backen.api.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author jiyec
 * @Date 2021/10/11 22:41
 * @Version 1.0
 **/
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/123")
    public void test(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getPathInfo());
    }
}
