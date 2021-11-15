package cn.wecuit.robot.utils.unirun.run;

import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.robot.utils.unirun.entity.AppConfig;
import cn.wecuit.robot.utils.unirun.entity.NewRecordBody;
import cn.wecuit.robot.utils.unirun.entity.Response;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.ClubInfo;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.RunStandard;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.SchoolBound;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.UserInfo;
import cn.wecuit.robot.utils.unirun.utils.MD5Utils;
import cn.wecuit.robot.utils.unirun.utils.SignUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/17 10:49
 * @Version 1.0
 **/
public class Request {
    private final HttpUtil2 http = new HttpUtil2();
    private final String appKey = "389885588s0648fa";
    private String token;
    private AppConfig config;

    public Request(String token, AppConfig config){
        this.token = token;
        this.config = config;
    }

    public UserInfo login(String phone, String password){
        String pass = MD5Utils.stringToMD5(password);
        String API = "https://run-lb.tanmasports.com/v1/auth/login/password";
        try {
            Map<String, String> body = new HashMap<>();
            body.put("appVersion", config.getAppVersion());
            body.put("brand", config.getBrand());
            body.put("deviceToken", config.getDeviceToken());
            body.put("deviceType", config.getDeviceType());
            body.put("mobileType", config.getMobileType());
            body.put("password", pass);
            body.put("sysVersion", config.getSysVersion());
            body.put("userPhone", phone);

            Map<String, String> headers = new HashMap<>();
            String bodyStr = JsonUtil.obj2String(body);
            String sign = SignUtils.get(null, bodyStr);
            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");
            byte[] bytes = http.doPostJson2Byte(API, headers, bodyStr);
            Response<UserInfo> userInfoResponse = JsonUtil.string2Obj(new String(bytes), new TypeReference<Response<UserInfo>>() {});
            int code = userInfoResponse.getCode();
            if(code == 10000){
                UserInfo userInfo = userInfoResponse.getResponse();
                this.token = userInfo.getOauthToken().getToken();
                return userInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInfo getUserInfo(){
        String API = "https://run-lb.tanmasports.com/v1/auth/query/token";
        try {
            Map<String, String> headers = new HashMap<>();
            String sign = SignUtils.get(null, null);
            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");
            String tokenInfo = http.doGet2(API, headers);
            Response<UserInfo> userInfoResponse = JsonUtil.string2Obj(tokenInfo, new TypeReference<Response<UserInfo>>() {});
            int code = userInfoResponse.getCode();
            if(code == 10000){
                return userInfoResponse.getResponse();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SchoolBound[] getSchoolBound(){

        String API = "https://run-lb.tanmasports.com/v1/unirun/querySchoolBound?schoolId=3680";
        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("schoolId", "3680");
            String sign = SignUtils.get(params, null);
            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");
            String tokenInfo = http.doGet2(API, headers);
            Response<SchoolBound[]> standardResponse = JsonUtil.string2Obj(tokenInfo, new TypeReference<Response<SchoolBound[]>>() {});
            return standardResponse.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RunStandard getRunStandard(){

        String API = "https://run-lb.tanmasports.com/v1/unirun/query/runStandard?schoolId=3680";
        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("schoolId", "3680");
            String sign = SignUtils.get(params, null);
            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");
            String tokenInfo = http.doGet2(API, headers);
            Response<RunStandard> standardResponse = JsonUtil.string2Obj(tokenInfo, new TypeReference<Response<RunStandard>>() {});
            return standardResponse.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ClubInfo[] getActivityList(String studentId){
        String schoolId = "3680";
        // 今天日期 年-月-日
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        Date date = new Date();
        String today = sdf.format(date);

        String API = String.format("https://run-lb.tanmasports.com/v1/clubactivity/queryActivityList?queryTime=%s&studentId=%s&schoolId=%s&pageNo=1&pageSize=15", today, studentId, schoolId);
        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("queryTime", today);
            params.put("studentId", studentId);
            params.put("schoolId", "3680");
            params.put("pageNo", "1");
            params.put("pageSize", "15");

            String sign = SignUtils.get(params, null);

            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");

            String tokenInfo = http.doGet2(API, headers);

            Response<ClubInfo[]> standardResponse = JsonUtil.string2Obj(tokenInfo, new TypeReference<Response<ClubInfo[]>>() {});
            return standardResponse.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String recordNew(NewRecordBody body){
        String API = "https://run-lb.tanmasports.com/v1/unirun/save/run/record/new";
        try {
            Map<String, String> headers = new HashMap<>();
            String bodyStr = JsonUtil.obj2String(body);
            String sign = SignUtils.get(null, bodyStr);
            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");
            byte[] bytes = http.doPostJson2Byte(API, headers, bodyStr);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
