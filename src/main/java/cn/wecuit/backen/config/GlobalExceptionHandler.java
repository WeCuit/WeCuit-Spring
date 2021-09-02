package cn.wecuit.backen.config;

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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        e.printStackTrace();
        return new ResponseResult(ResponseCode.SERVICE_ERROR.getCode(), ResponseCode.SERVICE_ERROR.getMsg(), null);
    }

    @ExceptionHandler({BaseException.class})
    public ResponseResult handleBaseException(BaseException e) {
        e.printStackTrace();
        ResponseCode code = e.getCode2();
        return new ResponseResult(code.getCode(), code.getMsg(), null);
    }

    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseResult handleDuplicateKeyException(DuplicateKeyException e) {
        e.printStackTrace();
        return new ResponseResult(ResponseCode.RESOURCE_ALREADY_EXIST.getCode(), ResponseCode.RESOURCE_ALREADY_EXIST.getMsg(), null);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseResult handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return new ResponseResult(ResponseCode.SERVICE_ERROR.getCode(), ResponseCode.SERVICE_ERROR.getMsg(), null);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        e.printStackTrace();
        return new ResponseResult(ResponseCode.METHOD_NOT_SUPPORTED.getCode(), ResponseCode.METHOD_NOT_SUPPORTED.getMsg(), null);
    }

    /**
     * 系统异常处理，比如：404,500
     *
     * @param response
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseResult handleException(HttpServletResponse response, Exception e) {
        e.printStackTrace();
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            response.setStatus(404);
            return new ResponseResult(ResponseCode.NO_HANDLER_FOUND.getCode(), ResponseCode.NO_HANDLER_FOUND.getMsg(), null);
        } else {
            response.setStatus(500);
            return new ResponseResult(ResponseCode.SERVICE_ERROR.getCode(), e.getMessage(), null);
        }
    }
}
