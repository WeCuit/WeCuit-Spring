package cn.wecuit.backen.services.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.pojo.AdminUser;
import cn.wecuit.backen.pojo.MiniUser;
import cn.wecuit.backen.pojo.Temporary;
import cn.wecuit.backen.response.ResponseCode;
import cn.wecuit.backen.services.*;
import cn.wecuit.backen.utils.JsonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/29 21:09
 * @Version 1.0
 **/
@Service
public class AuthServiceImpl implements AuthService {
    @Resource
    TencentService tencentService;
    @Resource
    TemporaryService temporaryService;
    @Resource
    AdminUserService adminUserService;
    @Resource
    MiniUserService miniUserService;

    @Override
    public Map<String, Object> genQRCode(MiniType type, String id, String client) {
        // 生成token
        String token = RandomStringUtils.random(30, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$()*+,/:;=?-._~");
        // 请求小程序码
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        String qrtoken;
        try {
            qrtoken = URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("小程序码生成失败");
        }
        if(type == MiniType.WX){
            String accessToken = tencentService.WX_getAccessToken();
            byte[] miniCode = tencentService.WX_acode_getUnlimited(accessToken, new HashMap<String, String>() {{
                put("scene", token);
                put("page", "pages/index/index");
            }});
            if('{' == (char)miniCode[0] && '"' == (char) miniCode[1]) {
                System.out.println(new String(miniCode));
                Map<String, Object> map = JsonUtil.string2Obj(new String(miniCode), Map.class);
                throw new BaseException(500, (String) map.get("errmsg"));
            }

            result.put("img", "data:image/png;base64," + Base64.getEncoder().encodeToString(miniCode));
        }else if (type == MiniType.QQ){
            String path = String.format("/pages/auth/auth?scene=%s", qrtoken);
            String miniURL = tencentService.QQ_getMiniURL(path);
            result.put("url", miniURL);
        }
        // 存储token
        boolean insert = temporaryService.addNew(new Temporary() {{
            setName("login_" + token);
            String value = "wait1," + client + ",";
            if(id != null)value += id;
            setValue(value);
            setTime(new Date(System.currentTimeMillis() + 65 * 1000)); // 65秒过期
        }});
        if(insert)return result;
        // 插入失败丢弃数据
        return null;
    }

    @Override
    public int checkToken(String token) {
        Temporary tokenInfo = temporaryService.getByName("login_" + token);
        if(tokenInfo == null) return -2;    // token不存在

        if (tokenInfo.getTime().getTime() < System.currentTimeMillis()) {
            temporaryService.deleteByName("login_" + token);
            return -1;  // 过期
        }

        String t = tokenInfo.getValue();

        if(t.startsWith("wait1"))return 0;    // 等待扫描
        if(t.startsWith("wait2"))return 3;    // 已扫描，等待授权结果

        if(t.startsWith("reject")) return 1;   // 拒绝授权

        return 2;   // 授权成功
    }

    @Override
    public boolean updateLoginStatus(String token, String result) {
        return temporaryService.updateByName(new Temporary(){{
            setName("login_" + token);
            setValue(result);
            setTime(new Date(System.currentTimeMillis() + 30 * 1000));
        }});
    }

    @Override
    public Map<String, Object> getTokenInfo(String token) {
        Temporary t = temporaryService.getByName("login_" + token);
        if(t == null  || t.getValue() == null)throw new BaseException(ResponseCode.USER_TOKEN_INVALID);
        if(t.getTime().getTime() < System.currentTimeMillis()){
            throw new BaseException(ResponseCode.USER_TOKEN_INVALID);
        }
        // wait1,[ADMIN|MINI],uid
        String[] split = t.getValue().split(",");
        t.setValue(t.getValue().replace("wait1", "wait2"));
        temporaryService.updateByName(t);
        Map<String, Object> result = new HashMap<>();
        if(split.length <= 2){
            result.put("type", "login");
        }else{
            String displayName;
            result.put("type", "bind");
            long id = Long.parseLong(split[2]);
            if("ADMIN".equals(split[1])) {
                AdminUser user;
                if("QQ".equals(split[0]))
                    user = adminUserService.info(id);
                else
                    user = adminUserService.info(id);
                displayName = user.getNickname();
            }else{
                MiniUser userById = miniUserService.getUserById(id);
                displayName = userById.getStuId();
            }
            result.put("displayName", displayName);
        }
        return result;
    }

    @Override
    public String[] adminLoginByLoginToken(String token) {

        Temporary t = temporaryService.getByName("login_" + token);
        temporaryService.deleteByName(t.getName());
        AdminUser user = null;
        String[] value = t.getValue().split(",");
        if("WX".equals(value[0])) {
            user = adminUserService.getUserByWxId(value[1]);
        }else if("QQ".equals(value[0])){
            user = adminUserService.getUserByQqId(value[1]);
        }
        if(user == null)throw new BaseException(ResponseCode.USER_NOT_EXIST);

        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        tokenInfo.setLoginType("ADMIN");

        // SaSession session = StpUtil.getSession();
        // session.set("role", null);

        String tokenName = tokenInfo.getTokenName();
        String tokenValue = tokenInfo.getTokenValue();
        return new String[]{tokenName, tokenValue};
    }

    @Override
    public boolean bindAdminByToken(String token, long id) {
        // token -> openid
        // adminId <--> openid
        Temporary t = temporaryService.getByName("login_" + token);
        if(t == null || t.getValue() == null)throw new BaseException(ResponseCode.USER_TOKEN_INVALID);
        if(t.getTime().getTime() < System.currentTimeMillis())throw new BaseException(ResponseCode.USER_TOKEN_INVALID);

        String[] split = t.getValue().split(",");
        String client = split[0];
        String openid = split[1];
        boolean b = adminUserService.updateInfoById(new AdminUser() {{
            setId(id);
            if ("WX".equals(client)) setWxId(openid);
            if ("QQ".equals(client)) setQqId(openid);
        }});
        return b;
    }

    @Override
    public String getOpenidByCode(String code, MiniType type) {
        Map<String, Object> session;
        if (type == MiniType.WX)
            session = tencentService.WX_code2session(code);
        else if (type == MiniType.QQ)
            session = tencentService.QQ_code2session(code);
        else
            throw new RuntimeException("不支持的客户端");

        // 判断请求失败
        if(session.containsKey("errcode")) {
            int errcode = (int) session.get("errcode");
            if (errcode != 0) throw new RuntimeException((String) session.get("errmsg"));
        }

        Object openid;
        if(session.containsKey("unionid"))
            openid = session.get("unionid");
        else
            openid = session.get("openid");
        return (String) openid;

    }

    @Override
    public String[] miniUserLogin(String openid, MiniType type) {
        MiniUser user = miniUserService.getUserByOpenid(openid, type);
        return this.miniUserLogin(user);
    }

    @Override
    public String[] miniUserLogin(MiniUser user) {
        if(user == null) {
            throw new BaseException(ResponseCode.USER_NOT_EXIST);
        }
        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        tokenInfo.setLoginType("MINI");

        return new String[]{tokenInfo.getTokenName(), tokenInfo.getTokenValue()};
    }
}
