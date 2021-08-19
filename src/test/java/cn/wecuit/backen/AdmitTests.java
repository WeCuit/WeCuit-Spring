package cn.wecuit.backen;

import cn.wecuit.backen.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/19 22:15
 * @Version 1.0
 **/
public class AdmitTests {
    @Test
    public void query() {
        URL resource = this.getClass().getResource("/admit.html");
        String path = resource.getPath();
        String s = FileUtil.ReadFile(path);
        System.out.println(s);
        System.out.println("======开始处理======");
        JXDocument jxDocument = JXDocument.create(s);
        // JXNode jxNode = jxDocument.selNOne("//*[@id=\"form1\"]/table/tbody/tr[3]/td/div/table/tbody/tr/td/b/text()");
        // System.out.println(jxNode.asString());
        // JXNode jxNode1 = jxDocument.selNOne("//*[@id=\"form1\"]/table/tbody/tr[1]/td/table/tbody/tr/td/span/text()");
        // System.out.println(jxNode1.asString());
        List<JXNode> jxNodes = jxDocument.selN("//*[@id=\"form1\"]/table/tbody/tr[5]/td/table/tbody/tr[2]/td/table/tbody/tr/td[not(@*) and string-length(text())>2 and string-length(text())<20]/text()");

        List<Map<String, String>> list = new LinkedList<>();
        for (int i = 0; i < jxNodes.size(); i++) {
            JXNode site = jxNodes.get(i++);
            JXNode lowest = jxNodes.get(i);
            list.add(new HashMap<String, String>(){{
                put("site", site.asString());
                put("lowest", lowest.asString());
            }});
        }

    }
}
