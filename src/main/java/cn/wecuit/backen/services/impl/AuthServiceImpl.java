package cn.wecuit.backen.services.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.wecuit.backen.bean.User;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.mapper.RoleMapper;
import cn.wecuit.backen.mapper.UserMapper;
import cn.wecuit.backen.response.ResponseCode;
import cn.wecuit.backen.services.AuthService;
import cn.wecuit.backen.utils.AESUtil;
import cn.wecuit.backen.utils.RSAUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/9/5 8:14
 * @Version 1.0
 **/
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${wecuit.aes.key}")
    private String sKey;
    @Resource
    UserMapper userMapper;
    @Resource
    RoleMapper roleMapper;

    @Override
    public String[] login(User user) {
        try {
            user.setStuPass(AESUtil.Decrypt(user.getStuPass(), sKey));
            if(user.getStuPass() == null )
            throw new BaseException(500, "密码解密失败");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(500, "密码解密失败");
        }
        // 数据库根据账号查密码
        User user1 = userMapper.selectOne(new QueryWrapper<User>() {{
            eq("stu_id", user.getStuId());
            select("id", "stu_pass");
        }});
        if (user1 == null) {
            //    用户不存在
            throw new BaseException(ResponseCode.USER_NOT_EXIST);
        }
        // 解密RSA密码
        String stuRealPassRSA = user1.getStuPass();
        String stuRealPass;
        try {
            stuRealPass = RSAUtils.decryptRSAByPriKey(stuRealPassRSA);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ResponseCode.USER_PASS_WRONG);
        }
        // 比对
        if (stuRealPass == null || !stuRealPass.equals(user.getStuPass())) {
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

        return userMapper.getUserMenuPath(id);
    }
}
