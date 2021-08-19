package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.services.TencentService;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.JsonUtil;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/17 20:14
 * @Version 1.0
 **/
@Service
public class TencentServiceImpl implements TencentService {
    @Value("${wecuit.wx.appid}")
    String WX_APPID;
    @Value("${wecuit.wx.secret}")
    String WX_SECRET;
    @Value("${wecuit.qq.appid}")
    String QQ_APPID;
    @Value("${wecuit.qq.secret}")
    String QQ_SECRET;
    private String[] APPID = new String[]{WX_APPID, QQ_APPID};
    private String[] SECRET = new String[]{WX_SECRET, QQ_SECRET};
    @Resource
    HttpServletRequest request;

    @Override
    public Map<String, Object> code2session(String code, int client) {
        String[] API = {
                "https://api.weixin.qq.com/sns/jscode2session?",
                "https://api.q.qq.com/sns/jscode2session?"
        };
        String reqUrl = API[client];
        reqUrl += "grant_type=authorization_code";
        reqUrl += "&appid=" + APPID[client];
        reqUrl += "&secret=" + SECRET[client];
        reqUrl += "&js_code=" + code;

        String get;
        try {
            get = HttpUtil.doGet(reqUrl);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("通信失败");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("解析失败");
        }
        return JsonUtil.string2Obj(get, Map.class);
    }
}
