package cn.wecuit.backen.services;

import cn.wecuit.backen.entity.MiniType;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/17 20:08
 * @Version 1.0
 **/
public interface MiniService {
    Map<String, Object> WX_code2session(String code);
    Map<String, Object> QQ_code2session(String code);

    /**
     *
     * @return Map
     * access_token 	string 	获取到的凭证
     * expires_in 	number 	凭证有效时间，单位：秒。目前是7200秒之内的值。
     * errcode 	number 	错误码
     * errmsg 	string 	错误信息
     */
    String WX_getAccessToken();
    byte[] WX_acode_getUnlimited(String accessToken, Map<String, String> body);
    String QQ_getMiniURL(String path);
    MiniType getMiniType(HttpServletRequest request);
}
