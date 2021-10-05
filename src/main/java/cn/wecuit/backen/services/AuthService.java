package cn.wecuit.backen.services;

import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.pojo.MiniUser;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/29 21:09
 * @Version 1.0
 **/
public interface AuthService {
    /**
     * 生成小程序码与token
     *
     * @return
     * @param type
     * @param id
     */
    Map<String, Object> genQRCode(MiniType type, String id, String client);

    /**
     *
     * @param token
     * @return [-2 不存在 | -1 过期 | 0 等待扫描 | 1 拒绝授权 | 2 授权成功 | 3 已扫描，等待授权结果]
     */
    int checkToken(String token);

    boolean updateLoginStatus(String token, String result);

    Map<String, Object> getTokenInfo(String token);
    String[] adminLoginByLoginToken(String token);

    /**
     * 管理员账号绑定微信QQ
     *
     * @param token
     * @param id
     * @return
     */
    boolean bindAdminByToken(String token, long id);
    boolean bindMiniByToken(String token, String openid, MiniType idType);

    String getOpenidByCode(String code, MiniType type);

    Map<String, Object> miniUserLogin(MiniUser user);
}
