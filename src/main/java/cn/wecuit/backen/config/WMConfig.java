package cn.wecuit.backen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author jiyec
 * @Date 2021/8/9 8:08
 * @Version 1.0
 **/
@Configuration
public class WMConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决Swagger无法访问的问题

        // registry.addResourceHandler("/swagger-ui.html")
        //         .addResourceLocations("classpath:/META-INF/resources/", "/static", "/public");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/", "/static", "/public");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
