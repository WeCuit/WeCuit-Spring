package cn.wecuit.backen.response;

import lombok.Getter;

/**
 * @Author jiyec
 * @Date 2021/8/27 17:16
 * @Version 1.0
 **/
@Getter
public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS(200, "success"),
    /**
     * 资源不存在
     */
    RESOURCES_NOT_EXIST(404, "资源不存在"),
    SERVICE_ERROR(500, "服务器异常"),
    NO_HANDLER_FOUND(404, "未找到处理器"),
    RESOURCE_ALREADY_EXIST(501, "资源已存在");

    private final int code;
    private final String msg;

    ResponseCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
