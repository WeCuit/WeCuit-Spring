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
    METHOD_NOT_SUPPORTED(405, "不支持的请求方式"),
    NO_HANDLER_FOUND(404, "未找到处理器"),
    RESOURCE_ALREADY_EXIST(501, "资源已存在"),

    /**
     * 用户
     */
    USER_NOT_EXIST(1404, "用户不存在"),
    USER_NOT_LOGIN(401, "用户未登录"),
    USER_PASS_WRONG(401, "用户密码错误"),
    USER_LOGIN_FAILED(401, "用户登录失败"),
    USER_NOT_AUTHORIZED(403, "用户无权限"),
    USER_TOKEN_INVALID(405, "token无效"),

    Theol_NOT_LOGIN(21401, "教学平台未登录"),
    /**
     * 文章
     */
    ARTICLE_NOT_FOUND(5404, "文章不存在");

    private final int code;
    private final String msg;

    ResponseCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
