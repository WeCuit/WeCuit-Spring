package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.services.CardService;
import cn.wecuit.backen.utils.JsonUtil;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/5 16:40
 * @Version 1.0
 **/
@BaseResponse
@ApiSupport(author = "jiyecafe@gmail.com")
@RestController
@RequestMapping("/Card")
public class CardController {

    @Resource
    CardService cardService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> d) throws IOException, ParseException {
        String cookie = d.get("cookie");
        return cardService.login(cookie);
    }

    @PostMapping("/getAccWallet")
    public Map getAccWallet(@RequestBody Map<String, String> d) throws IOException, ParseException {
        String accNum = d.get("AccNum");
        String accWallet = cardService.getAccWallet(accNum);

        Map map = JsonUtil.string2Obj(accWallet, Map.class);
        String code = (String) map.get("Code");
        if ("1".equals(code)) {
            return map;
        } else {
            throw new BaseException((String) map.get("Msg"));
        }
    }

    @PostMapping("/getDealRec")
    public Map getDealRec(@RequestBody Map<String, String> d) throws IOException, ParseException {
        String dealRec = cardService.getDealRec(d);
        Map map = JsonUtil.string2Obj(dealRec, Map.class);

        String code = (String)map.get("Code");
        if("1".equals(code)){
            return map;
        }else{
            throw new BaseException(Integer.parseInt(code), (String) map.get("Msg"));
        }
    }
}
