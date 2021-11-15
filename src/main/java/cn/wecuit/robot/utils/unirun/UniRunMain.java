package cn.wecuit.robot.utils.unirun;

import cn.wecuit.robot.utils.unirun.entity.AppConfig;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.ClubInfo;
import cn.wecuit.robot.utils.unirun.entity.ResponseType.UserInfo;
import cn.wecuit.robot.utils.unirun.run.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
        String phone = "";
        String password = "";
        // 型号仓库： https://github.com/KHwang9883/MobileModels
        AppConfig config = new AppConfig() {{
            setAppVersion("1.8.0");     // APP版本，一般不做修改
            setBrand("");         // 手机品牌
            setMobileType("");   // 型号
            setSysVersion("10");        // 系统版本
        }};

        Request request = new Request("", config);
        UserInfo userInfo = request.login(phone, password);
        long userId = userInfo.getUserId();
        if (userId != -1) {
            ClubInfo[] activityList = request.getActivityList(String.valueOf(userId));
            List<ClubInfo> list = new ArrayList<>();
            for (ClubInfo clubInfo : activityList) {
                if(clubInfo.getSignInStudent() < clubInfo.getMaxStudent()){
                    list.add(clubInfo);
                }
            }
        } else {
            System.out.println("用户Id获取失败");
        }
    }

    public static List<ClubInfo> getAvailableActivityList(String token){
        AppConfig config = new AppConfig() {{
            setAppVersion("1.8.0");     // APP版本，一般不做修改
            setBrand("");         // 手机品牌
            setMobileType("");   // 型号
            setSysVersion("10");        // 系统版本
        }};
        Request request = new Request(token, config);
        UserInfo userInfo = request.getUserInfo();
        long studentId = userInfo.getStudentId();
        List<ClubInfo> list = new ArrayList<>();
        if (studentId != -1) {
            ClubInfo[] activityList = request.getActivityList(String.valueOf(studentId));
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
}