package cn.wecuit.backen.exception;

import cn.wecuit.backen.response.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类，继承运行时异常，确保事务正常回滚
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException{
    private Integer code;
    private ResponseCode code2;

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }
    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(ResponseCode code) {
        this.code2 = code;
    }
    public BaseException(Throwable cause, ResponseCode code) {
        super(cause);
        this.code = code.getCode();
    }


}
