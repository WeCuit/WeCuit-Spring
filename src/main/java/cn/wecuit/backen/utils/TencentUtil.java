package cn.wecuit.backen.utils;

import cn.wecuit.backen.utils.HTTP.HttpUtil;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Map;

public class TencentUtil {

    private static String[] APPID;
    private static String[] SECRET;


    public static void initAppid(String wx, String qq){
        APPID = new String[]{wx,qq};
    }
    public static void initSecret(String wx, String qq){
        SECRET = new String[]{wx,qq};
    }

    public static Map<String, Object> getAccessToken(int client){
        String[] API = {
                "https://api.weixin.qq.com/cgi-bin/token?",
                "https://api.q.qq.com/api/getToken?"
        };
        String reqUrl = API[client];
        reqUrl += "grant_type=client_credential";
        reqUrl += "&appid=" + APPID[client];
        reqUrl += "&secret=" + SECRET[client];

        // try {
        //     Response response = HttpUtil.httpGet(reqUrl);
        //     return JsonUtil.string2Obj(Objects.requireNonNull(response.body()).string(), Map.class);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        return null;
    }

    public static Map<String, Object> sendSub(int client){
        String[] API = {
                "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=",
                "https://api.q.qq.com/api/json/subscribe/SendSubscriptionMessage?access_token="
        };

        String reqUrl = API[client] + "AT";
        String fieldData = "access_token=";
        fieldData += "&touser=";
        fieldData += "&template_id=";
        fieldData += "&page=";
        fieldData += "&data=";
        fieldData += "&form_id=";    // TODO: 检查兼容性

        // try {
        //     Response response = HttpUtil.httpPost(reqUrl, fieldData);
        //     return JsonUtil.string2Obj(Objects.requireNonNull(response.body()).string(), Map.class);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        return null;
    }
}
