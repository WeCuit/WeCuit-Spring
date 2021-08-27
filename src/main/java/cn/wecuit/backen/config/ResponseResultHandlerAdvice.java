package cn.wecuit.backen.config;

import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.response.ResponseCode;
import cn.wecuit.backen.response.ResponseResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应体处理器
 * @Author jiyec
 * @Date 2021/8/27 18:20
 * @Version 1.0
 **/
@RestControllerAdvice(annotations = BaseResponse.class)
public class ResponseResultHandlerAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if(MediaType.APPLICATION_JSON.equals(mediaType)){
            // 判断响应的Content-Type为JSON格式的body

            if(body instanceof ResponseResult){ // 如果响应返回的对象为统一响应体，则直接返回body
                return body;
            }else{
                // 只有正常返回的结果才会进入这个判断流程，所以返回正常成功的状态码
                return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), body);
            }
        }
        // 非JSON格式body直接返回即可
        return body;
    }
}
