package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.config.StpMiniUtil;
import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.pojo.MiniUser;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.AuthService;
import cn.wecuit.backen.services.MiniUserService;
import io.swagger.annotations.ApiOperation;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/9/28 9:15
 * @Version 1.0
 **/
@RestController
@BaseResponse
@RequestMapping("/auth")
public class MiniAuthController {

    @Resource
    AuthService authService;
    @Resource
    MiniUserService miniUserService;

    @ApiOperation("小程序发送授权结果")
    @PostMapping("/mini/result")
    public Map<String, Object> miniLoginResult(@RequestBody Map<String, String> body,
            HttpServletRequest request){
        String referer = request.getHeader("referer");
        if (null == referer) throw new BaseException(20500, "请求异常");
        MiniType type;
        if (referer.contains("servicewechat.com"))
            type = MiniType.WX;
        else if (referer.contains("appservice.qq.com"))
            type = MiniType.QQ;
        else
            throw new RuntimeException("不支持的客户端");
        // openid 为空表示拒绝
        boolean ret;
        String token = body.get("token");
        String code = body.get("code");

        String openid = authService.getOpenidByCode(code, type);
        if(openid == null)
            ret = authService.updateLoginStatus(token, "reject");
        else
            ret = authService.updateLoginStatus(token, type.name() + "," + openid);
        boolean finalRet = ret;
        return new HashMap<String, Object>(){{
            put("result", finalRet);
        }};
    }

    @ApiOperation("获取小程序登录授权信息")
    @GetMapping("/mini/tokenInfo")
    public Map<String, Object> miniTokenInfo(@RequestParam String token, HttpServletRequest request){
        String referer = request.getHeader("referer");
        if (null == referer) throw new BaseException(20500, "请求异常");
        return authService.getTokenInfo(token);
    }

    /**
     * 小程序用户登录
     * openid | 是否管理员[暂废]
     *
     * @throws IOException
     */
    @GetMapping("/getAccessToken")
    public Map<String, Object> getAccessToken(HttpServletRequest request, @RequestParam String code) throws IOException, ParseException {

        String referer = request.getHeader("referer");
        if (null == referer) throw new BaseException(20500, "请求异常");
        MiniType type;
        if (referer.contains("servicewechat.com"))
            type = MiniType.WX;
        else if (referer.contains("appservice.qq.com"))
            type = MiniType.QQ;
        else
            throw new RuntimeException("不支持的客户端");

        String openid = authService.getOpenidByCode(code, type);

        MiniUser user = miniUserService.getUserByOpenid(openid, type);

        Map<String, Object> auth = authService.miniUserLogin(user);

        return new HashMap<String, Object>() {{
            put("token", auth);
            put("userInfo", new HashMap<String, Object>(){{
                put("uid", user.getId());
                if(user.getStuId() != null)
                    put("sid", user.getStuId());
            }});
            put("bind", new HashMap<String, Boolean>(){{
                put("QQ", user.getQqId() != null);
                put("WX", user.getWxId() != null);
                put("STU", user.getStuId() != null);
            }});
        }};
    }

    @GetMapping("/register")
    public Map<String, Object> register(HttpServletRequest request, @RequestParam String code){
        String referer = request.getHeader("referer");
        if (null == referer) throw new BaseException(20500, "请求异常");
        MiniType type;
        if (referer.contains("servicewechat.com"))
            type = MiniType.WX;
        else if (referer.contains("appservice.qq.com"))
            type = MiniType.QQ;
        else
            throw new RuntimeException("不支持的客户端");
        String openid = authService.getOpenidByCode(code, type);
        MiniUser miniUser = miniUserService.regUserByOpenid(openid, type);
        Map<String, Object> auth = authService.miniUserLogin(miniUser);

        return new HashMap<String, Object>() {{
            put("token", auth);
            put("userInfo", new HashMap<String, Object>(){{
                put("uid", miniUser.getId());
            }});
        }};
    }

    @ApiOperation("获取小程序绑定小程序码")
    @GetMapping("/binding/mini/{type}")
    public Map<String, Object> getMiniBindCode(@PathVariable MiniType type){
        String id = StpMiniUtil.getLoginIdAsString();
        return authService.genQRCode(type, id, "MINI");
    }

    @ApiOperation("绑定学校账号")
    @PostMapping("/binding/student")
    public Map<String, Object> bindStudent(@RequestBody Map<String, String> body){
        long id = StpMiniUtil.getLoginIdAsLong();
        String userId = body.get("userId");
        String userPass = body.get("userPass");

        boolean b = miniUserService.bindStudent(new MiniUser() {{
            setId(id);
            setStuId(userId);
            setStuPass(userPass);
        }});
        return  new HashMap<String, Object>(){{
            put("result", b);
        }};
    }

}
