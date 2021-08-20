package cn.wecuit.backen.config;

import cn.wecuit.backen.bean.ResponseData;
import cn.wecuit.backen.exception.BaseException;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author jiyec
 * @Date 2021/7/28 18:02
 * @Version 1.0
 **/
@RestControllerAdvice //@RestControllerAdvice 该注解定义全局异常处理类
public class GlobalExceptionHandler {
    @ExceptionHandler({IOException.class}) //@ExceptionHandler 该注解声明异常处理方法
    public ResponseData baseException(IOException e) throws Exception {
        e.printStackTrace();
        return new ResponseData(){{
            setCode(601);
            setError(e.getMessage());
        }};
    }
    @ExceptionHandler({RuntimeException.class}) //@ExceptionHandler 该注解声明异常处理方法
    public ResponseData defaultErrorHandler(RuntimeException e) throws Exception {
        e.printStackTrace();
        if(e instanceof BaseException){
            BaseException exception = (BaseException) e;
            return new ResponseData(){{
                setCode(exception.getCode());
                setError(exception.getMessage());
            }};
        }else if(e instanceof DuplicateKeyException){
            DuplicateKeyException exception = (DuplicateKeyException) e;
            return new ResponseData(){{
                setCode(500);
                setError("数据已存在！");
            }};
        }
        return new ResponseData(){{
            setCode(501);
            setError(e.getMessage());
        }};
    }
    /**
     * 系统异常处理，比如：404,500
     * @param response
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseData defaultErrorHandler(HttpServletResponse response, Exception e) throws Exception {
        e.printStackTrace();
        ResponseData r = new ResponseData();
        r.setError(e.getMessage());
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            response.setStatus(404);
            r.setCode(404);
        } else {
            response.setStatus(500);
            r.setCode(500);
        }
        r.setData(null);
        return r;
    }
}
