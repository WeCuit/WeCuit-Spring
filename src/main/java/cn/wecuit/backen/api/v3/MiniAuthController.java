package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.AuthService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @ApiOperation("获取小程序绑定用小程序码")
    @GetMapping("/binding/mini/{type}")
    public Map<String, Object> getMiniBindCode(@PathVariable MiniType type, @RequestParam String openid){
        // TODO: 获取当前小程序用户ID
        return authService.genQRCode(type, null, "MINI");
    }
}
