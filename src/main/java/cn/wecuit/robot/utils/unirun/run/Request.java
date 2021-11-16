package cn.wecuit.robot.utils.unirun.run;

import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.robot.utils.unirun.entity.AppConfig;
import cn.wecuit.robot.utils.unirun.entity.NewRecordBody;
import cn.wecuit.robot.utils.unirun.entity.Response;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.*;
import cn.wecuit.robot.utils.unirun.utils.MD5Utils;
import cn.wecuit.robot.utils.unirun.utils.SignUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/17 10:49
 * @Version 1.0
 **/
public class Request {
    private final HttpUtil2 http = new HttpUtil2();
    private final String appKey = "389885588s0648fa";
    private final String HOST = "https://run-lb.tanmasports.com/";
    private String token;
    private AppConfig config;

    public Request(String token, AppConfig config){
        this.token = token;
        this.config = config;
    }

    public UserInfo login(String phone, String password){
        String pass = MD5Utils.stringToMD5(password);
        String API = HOST + "v1/auth/login/password";
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
        String API = HOST + "v1/auth/query/token";
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

        String API = HOST + "v1/unirun/querySchoolBound?schoolId=3680";
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

        String API = HOST + "v1/unirun/query/runStandard?schoolId=3680";
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

    public List<ClubInfo> getActivityList(String studentId, String date){
        String schoolId = "3680";

        String API = String.format(HOST + "v1/clubactivity/queryActivityList?queryTime=%s&studentId=%s&schoolId=%s&pageNo=1&pageSize=15", date, studentId, schoolId);
        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("queryTime", date);
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

            Response<List<ClubInfo>> standardResponse = JsonUtil.string2Obj(tokenInfo, new TypeReference<Response<List<ClubInfo>>>() {});
            return standardResponse.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JoinClubResult joinClub(String studentId, String activityId){

        String API = String.format(HOST + "v1/clubactivity/joinClubActivity?studentId=%s&activityId=%s", studentId, activityId);
        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("studentId", studentId);
            params.put("activityId", activityId);

            String sign = SignUtils.get(params, null);

            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");

            String joinResult = http.doGet2(API, headers);

            Response<JoinClubResult> joinClubResponse = JsonUtil.string2Obj(joinResult, new TypeReference<Response<JoinClubResult>>() {});
            return joinClubResponse.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<SportsClassStudentLearnClockingV0> getMySportsClassClocking(){

        String API = HOST + "v1/sports/class/getMySportsClassClocking";
        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> params = new HashMap<>();

            String sign = SignUtils.get(params, null);

            headers.put("sign", sign);
            headers.put("token", token);
            headers.put("appkey", appKey);
            headers.put("Content-Type", "application/json; charset=UTF-8");

            String joinResult = http.doGet2(API, headers);

            Response<List<SportsClassStudentLearnClockingV0>> sportsClassStudentLearnClockingV0Response = JsonUtil.string2Obj(joinResult, new TypeReference<Response<List<SportsClassStudentLearnClockingV0>>>() {});
            return sportsClassStudentLearnClockingV0Response.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String recordNew(NewRecordBody body){
        String API = HOST + "v1/unirun/save/run/record/new";
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
