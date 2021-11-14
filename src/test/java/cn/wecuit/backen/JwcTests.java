package cn.wecuit.backen;

import cn.wecuit.backen.utils.FileUtil;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.JwcUtil;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/11/14 20:43
 * @Version 1.0
 **/
public class JwcTests {
    @Test
    public void labTest(){
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/JWC/lab_detail.html");
        String html = FileUtil.ReadFile(resourceAsStream);
        Map<String, Object> stringObjectMap = JwcUtil.LAB_DetailHtml2json(html);
        String s = JsonUtil.obj2String(stringObjectMap.get("list"));
        System.out.println(s);
    }
}
