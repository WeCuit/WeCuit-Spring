package cn.wecuit.backen.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.response.ResponseCode;
import cn.wecuit.backen.response.ResponseResult;
import cn.wecuit.backen.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 异常处理器
 *
 * @Author jiyec
 * @Date 2021/7/28 18:02
 * @Version 1.0
 **/
//@RestControllerAdvice 该注解定义全局异常处理类
@ResponseBody
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({IOException.class})
    public ResponseResult handleIOException(IOException e) {
        return new ResponseResult(ResponseCode.SERVICE_ERROR.getCode(), ResponseCode.SERVICE_ERROR.getMsg(), null);
    }

    @ExceptionHandler({BaseException.class})
    public ResponseResult handleBaseException(BaseException e) {
        ResponseCode code = e.getCode2();
        if (code != null)
            return new ResponseResult(code.getCode(), code.getMsg(), null);
        else
            return new ResponseResult(e.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseResult handleDuplicateKeyException(DuplicateKeyException e) {
        return new ResponseResult(ResponseCode.RESOURCE_ALREADY_EXIST.getCode(), ResponseCode.RESOURCE_ALREADY_EXIST.getMsg(), null);
    }

    @ExceptionHandler({NotLoginException.class})
    public ResponseResult handleRuntimeException(NotLoginException e) {
        return new ResponseResult(ResponseCode.USER_NOT_LOGIN.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseResult handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return new ResponseResult(ResponseCode.SERVICE_ERROR.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ResponseResult(ResponseCode.METHOD_NOT_SUPPORTED.getCode(), ResponseCode.METHOD_NOT_SUPPORTED.getMsg(), null);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseResult handleNoHandlerFoundException(HttpServletRequest request, NoHandlerFoundException e) {
        return new ResponseResult(ResponseCode.NO_HANDLER_FOUND.getCode(), ResponseCode.NO_HANDLER_FOUND.getMsg(), new HashMap<String, Object>() {{
            put("uri", request.getRequestURI());
        }});
    }

    /**
     * 系统异常处理，比如：404,500
     *
     * @param response
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler({NullPointerException.class})
    public ResponseResult handleNullPointerException(HttpServletResponse response, NullPointerException e) {
        e.printStackTrace();
        response.setStatus(500);
        return new ResponseResult(ResponseCode.SERVICE_ERROR.getCode(), "空指针异常", null);
    }
    @ExceptionHandler({Exception.class})
    public ResponseResult handleException(HttpServletResponse response, Exception e) {
        e.printStackTrace();
        response.setStatus(500);
        return new ResponseResult(ResponseCode.SERVICE_ERROR.getCode(), e.getMessage(), null);
    }
}
