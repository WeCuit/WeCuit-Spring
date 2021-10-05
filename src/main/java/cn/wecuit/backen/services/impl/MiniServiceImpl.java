package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.mapper.TemporaryMapper;
import cn.wecuit.backen.pojo.Temporary;
import cn.wecuit.backen.services.MiniService;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/17 20:14
 * @Version 1.0
 **/
@Service
public class MiniServiceImpl implements MiniService {
    @Value("${wecuit.wx.appid}")
    String WX_APPID;
    @Value("${wecuit.wx.secret}")
    String WX_SECRET;
    @Value("${wecuit.qq.appid}")
    String QQ_APPID;
    @Value("${wecuit.qq.secret}")
    String QQ_SECRET;
    @Resource
    TemporaryMapper temporaryMapper;

    @Override
    public Map<String, Object> WX_code2session(String code) {

        String reqUrl = "https://api.weixin.qq.com/sns/jscode2session?";
        reqUrl += "grant_type=authorization_code";
        reqUrl += "&appid=" + WX_APPID;
        reqUrl += "&secret=" + WX_SECRET;
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

    @Override
    public Map<String, Object> QQ_code2session(String code) {
        String reqUrl = "https://api.q.qq.com/sns/jscode2session?";
        reqUrl += "grant_type=authorization_code";
        reqUrl += "&appid=" + QQ_APPID;
        reqUrl += "&secret=" + QQ_SECRET;
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
        System.out.println(get);
        return JsonUtil.string2Obj(get, Map.class);
    }

    @Override
    public String WX_getAccessToken() {
        String api = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", WX_APPID, WX_SECRET);
        Temporary temporary = temporaryMapper.selectOne(new QueryWrapper<Temporary>() {{
            eq("name", "wx_accesstoken");
        }});
        Date now = new Date();
        try {
//            不存在
            if (temporary == null) {
                String s = HttpUtil.doGet(api);
                Map<String, Object> result = new ObjectMapper().readValue(s, Map.class);

                if (result.containsKey("errcode")) return null;

                temporary = new Temporary();
                temporary.setName("wx_accesstoken");
                temporary.setValue((String) result.get("access_token"));
                temporary.setTime(new Date(now.getTime() + (int)result.get("expires_in") * 1000));
                temporaryMapper.insert(temporary);
            }else if (temporary.getTime().getTime() < now.getTime()) {
                // 更新
                String s = HttpUtil.doGet(api);
                Map<String, Object> result = new ObjectMapper().readValue(s, Map.class);

                if (result.containsKey("errcode")) return null;

                temporary.setValue((String) result.get("access_token"));
                temporary.setTime(new Date(now.getTime() + (int)result.get("expires_in") * 1000));
                temporaryMapper.updateById(temporary);
            }

            return temporary.getValue();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] WX_acode_getUnlimited(String accessToken, Map<String, String> body) {
        String API = String.format("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=%s", accessToken);
        try {
            String s = JsonUtil.obj2String(body);
            HttpUtil2 http = new HttpUtil2();
            return http.doPostJson2Byte(API, s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public String QQ_getMiniURL(String path) {
        try {
            path = URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format("https://m.q.qq.com/a/p/%s?s=%s", QQ_APPID, path);
    }

    @Override
    public MiniType getMiniType(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if(referer != null){
            if (referer.contains("servicewechat.com"))
                return MiniType.WX;
            else if (referer.contains("appservice.qq.com"))
                return MiniType.QQ;
            else
                throw new RuntimeException("不支持的客户端");
        }else{
            String ua = request.getHeader("user-agent");

            if (ua.contains("WeChat"))
                return MiniType.WX;
            else if (ua.contains("QQ"))
                return MiniType.QQ;
            else
                throw new RuntimeException("不支持的客户端");
        }
    }
}
