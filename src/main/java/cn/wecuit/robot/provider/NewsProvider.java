package cn.wecuit.robot.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/16 15:59
 * @Version 1.0
 **/
public class NewsProvider {

    public static String genLightJson(Map<String, String> news) throws UnsupportedEncodingException {
        String path = "pages/articleView/articleView";
        String finalPath = path + "?path=" + news.get("link")
                + "&source=" + news.get("source")
                + "&domain=" + news.get("domain");

        String encode = URLEncoder.encode(finalPath, "UTF-8");
        String content = "{\"app\":\"com.tencent.miniapp_01\",\"view\":\"view_8C8E89B49BE609866298ADDFF2DBABA4\",\"ver\":\"1.0.0.19\",\"prompt\":\"#prompt#\",\"meta\":{\"detail_1\":{\"appid\":\"1111006861\",\"title\":\"We成信大\",\"desc\":\"#description#\",\"icon\":\"https:\\/\\/miniapp.gtimg.cn\\/public\\/appicon\\/6dca4792d15ed27ac7dd436051b46209_200.jpg\",\"preview\":\"#preview#\",\"url\":\"#url#\",\"scene\":0,\"host\":{\"uin\":1690127128,\"nick\":\"msojocs\"},\"shareTemplateId\":\"8C8E89B49BE609866298ADDFF2DBABA4\",\"shareTemplateData\":{},\"showLittleTail\":\"\",\"gamePoints\":\"\",\"gamePointsUrl\":\"\"}},\"config\":{\"type\":\"normal\",\"width\":0,\"height\":0,\"forward\":0,\"autoSize\":0,\"ctime\":1635597993,\"token\":\"18c09e3ce985f36aeffacdfdcb74a30a\"}}";
        return content.replace("#description#", news.get("title"))
                // https://m.q.qq.com/a/p/{appid}?s={encodeURIComponent(path)}
                .replace("#prompt#", "[QQ小程序]" + news.get("title"))
                .replace("#url#", "m.q.qq.com/a/p/1111006861?s=" + encode)
                .replace("#preview#", "api.oick.cn/random/api.php?type=pc&_t=" + new Date().getTime());
    }
}
