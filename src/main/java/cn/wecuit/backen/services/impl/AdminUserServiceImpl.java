package cn.wecuit.backen.services.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.wecuit.backen.pojo.AdminUser;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.mapper.RoleMapper;
import cn.wecuit.backen.mapper.AdminUserMapper;
import cn.wecuit.backen.response.ResponseCode;
import cn.wecuit.backen.services.AdminUserService;
import cn.wecuit.backen.utils.AESUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/9/5 8:14
 * @Version 1.0
 **/
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Value("${wecuit.aes.key}")
    private String sKey;
    @Resource
    AdminUserMapper adminUserMapper;
    @Resource
    RoleMapper roleMapper;

    @Override
    public boolean register(AdminUser user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        int insert = adminUserMapper.insert(user);
        return insert == 1;
    }

    @Override
    public String[] login(AdminUser user) {
        try {
            user.setPassword(AESUtil.Decrypt(user.getPassword(), sKey));
            if(user.getPassword() == null )
                throw new BaseException(500, "密码解密失败");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(500, "密码解密失败");
        }
        // 数据库根据账号查密码
        AdminUser user1 = adminUserMapper.selectOne(new QueryWrapper<AdminUser>() {{
            eq("login", user.getLogin());
            select("id", "password");
        }});
        if (user1 == null) {
            //    用户不存在
            throw new BaseException(ResponseCode.USER_NOT_EXIST);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // 比对
        if (!passwordEncoder.matches(user.getPassword(), user1.getPassword())) {
            throw new BaseException(ResponseCode.USER_PASS_WRONG);
        }
        // 登录
        StpUtil.login(user1.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // SaSession session = StpUtil.getSession();
        // session.set("role", null);

        String tokenName = tokenInfo.getTokenName();
        String tokenValue = tokenInfo.getTokenValue();
        return new String[]{tokenName, tokenValue};
    }

    @Override
    public List<String> userMenu(long id) {
        return adminUserMapper.getUserMenuPath(id);
    }

    @Override
    public AdminUser info(long id) {
        return adminUserMapper.selectOne(new QueryWrapper<AdminUser>(){{
            eq("id", id);
            select("email", "login", "avatar", "nickname");
        }});
    }

    @Override
    public boolean modifyInfoById(long id, AdminUser user) {
        user.setId(id);
        int i = adminUserMapper.updateById(user);
        return i == 1;
    }

    @Override
    public AdminUser getUserByWxId(String openid) {
        return adminUserMapper.selectOne(new QueryWrapper<AdminUser>(){{
            eq("wx_id", openid);
            select("id", "nickname");
        }});
    }

    @Override
    public AdminUser getUserByQqId(String openid) {
        return adminUserMapper.selectOne(new QueryWrapper<AdminUser>(){{
            eq("qq_id", openid);
            select("id", "nickname");
        }});
    }

    @Override
    public boolean updateInfoById(AdminUser user) {
        int i = adminUserMapper.updateById(user);
        return i==1;
    }
}
