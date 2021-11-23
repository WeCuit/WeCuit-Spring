package cn.wecuit.robot.utils.unirun;

import cn.wecuit.robot.utils.unirun.entity.AppConfig;
import cn.wecuit.robot.utils.unirun.entity.Response;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.ClubInfo;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.SignInTf;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.SportsClassStudentLearnClockingV0;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.UserInfo;
import cn.wecuit.robot.utils.unirun.entity.SignInOrSignBackBody;
import cn.wecuit.robot.utils.unirun.run.Request;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 运行主体
 * 说明：
 * 本程序仅供学习交流使用，请在下载后24小时内及时删除。
 * 本程序不提供后续更新服务。
 * 若由使用本程序造成包括但不限于校方警告、课程分数计0、封号、勒令退学等不良后果，一切责任由使用者承当。
 * 使用本程序即代表使用者同意以上条款。
 *
 */
@Slf4j
public class UniRunMain {
    public static void main(String[] args) {
        String token = "1243489ade4c457702e7c9c7fe2698a0";
        AppConfig config = new AppConfig() {{
            setAppVersion("1.8.1");     // APP版本，一般不做修改
            setBrand("realme");         // 手机品牌
            setMobileType("RMX2117");   // 型号
            setSysVersion("10");        // 系统版本
        }};
        Request request = new Request(token, config);
        //UserInfo userInfo = request.login("13107975658", "1222");
        UserInfo userInfo = request.getUserInfo();

        // 今天日期 年-月-日
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        Date date = new Date();
        String today = sdf.format(date);
        List<ClubInfo> activityList = request.getActivityList(String.valueOf(userInfo.getStudentId()), today);
        List<SportsClassStudentLearnClockingV0> mySportsClassClocking = request.getMySportsClassClocking();
        log.info("{}", mySportsClassClocking);
    }

    public static List<ClubInfo> getAvailableActivityList(String phone, String password){
        AppConfig config = new AppConfig() {{
            setAppVersion("1.8.1");     // APP版本，一般不做修改
            setBrand("realme");         // 手机品牌
            setMobileType("RMX2117");   // 型号
            setSysVersion("10");        // 系统版本
        }};
        Request request = new Request("", config);
        Response<UserInfo> userInfoResponse = request.login(phone, password);
        UserInfo userInfo = userInfoResponse.getResponse();
        List<ClubInfo> list = new ArrayList<>();
        if (userInfo != null) {
            long studentId = userInfo.getStudentId();
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd");
            Date date = new Date(new Date().getTime() + 1000 * 6 * 24 * 60 * 60);
            String today = sdf.format(date);
            List<ClubInfo> activityList = request.getActivityList(String.valueOf(studentId), today);

            for (ClubInfo clubInfo : activityList) {
                if(clubInfo.getSignInStudent() < clubInfo.getMaxStudent()){
                    list.add(clubInfo);
                }
            }
        } else {
            log.error("用户Id获取失败");
        }
        return list;
    }

    public static Response joinClub(String phone, String password, String activityId){
        AppConfig config = new AppConfig() {{
            setAppVersion("1.8.1");     // APP版本，一般不做修改
            setBrand("realme");         // 手机品牌
            setMobileType("RMX2117");   // 型号
            setSysVersion("10");        // 系统版本
        }};
        Request request = new Request("", config);
        Response<UserInfo> userInfoResponse = request.login(phone, password);
        UserInfo userInfo = userInfoResponse.getResponse();
        if (userInfo != null) {
            Long studentId = userInfo.getStudentId();
            return request.joinClub(String.valueOf(studentId), activityId);
        } else {
            return userInfoResponse;
        }
    }

    public static Response signInOrSignBack(String phone, String password){
        AppConfig config = new AppConfig() {{
            setAppVersion("1.8.1");     // APP版本，一般不做修改
            setBrand("realme");         // 手机品牌
            setMobileType("RMX2117");   // 型号
            setSysVersion("10");        // 系统版本
        }};
        Request request = new Request("", config);
        Response<UserInfo> userInfoResponse = request.login(phone, password);
        UserInfo userInfo = userInfoResponse.getResponse();

        log.info("用户信息：{}", userInfo);
        if (userInfo != null) {
            Long studentId = userInfo.getStudentId();
            SignInTf signInTf = request.getSignInTf(String.valueOf(studentId));
            log.info("待签到俱乐部：{}", signInTf);
            String signStatus = signInTf.getSignStatus();
            String signInStatus = signInTf.getSignInStatus();
            String signBackStatus = signInTf.getSignBackStatus();

            // TODO: 待确认已签到且已签退？
            if("1".equals(signInStatus) && "1".equals(signBackStatus))return null;

            String signType;
            if("1".equals(signStatus)){
            //    可签到
                signType = "1";
            }else if("1".equals(signInStatus) && "0".equals(signStatus)){
            //    可签退
                signType = "2";
            }else{
                log.info("非可签到签退状态");
                return null;
            }

            SignInOrSignBackBody signInOrSignBackBody = new SignInOrSignBackBody(
                    signInTf.getActivityId(),
                    signInTf.getLatitude(),
                    signInTf.getLongitude(),
                    signType,
                    studentId);

            return request.signInOrSignBack(signInOrSignBackBody);
        } else {
            return userInfoResponse;
        }
    }
}