package cn.wecuit.backen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @Author jiyec
 * @Date 2021/9/28 18:55
 * @Version 1.0
 **/
public class AutoPrefixUrlMapping extends RequestMappingHandlerMapping {

    // 从配置文件中读取根目录
    @Value("${wecuit.api-package}")
    private String apiPackagePath;

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo mappingInfo =  super.getMappingForMethod(method, handlerType);
        if (mappingInfo != null){
            String prefix = this.getPrefix(handlerType);
            RequestMappingInfo newMappingInfo = RequestMappingInfo.paths(prefix).build().combine(mappingInfo);
            return newMappingInfo;
        }
        return mappingInfo;
    }

    // 获取前缀
    private String getPrefix(Class<?> handlerType){

        String packageName = handlerType.getPackage().getName();
        String newPath = packageName.replaceAll(this.apiPackagePath, "");

        return newPath.replace(".","/");
    }
}
