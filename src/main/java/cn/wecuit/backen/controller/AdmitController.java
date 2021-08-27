package cn.wecuit.backen.controller;

import cn.wecuit.backen.response.ResponseResult;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.utils.HTTP.HttpUtil2;
import org.apache.hc.core5.http.ParseException;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/19 21:43
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Admit")
public class AdmitController {
    @PostMapping("/query")
    public ResponseResult query(@RequestBody Map<String, String> d){
        HttpUtil2 httpUtil2 = new HttpUtil2();
        try {
            String html = httpUtil2.doPost("http://zjc.cuit.edu.cn/Zs/LqXsCx.asp", d, "GB2312");
            JXDocument jxDocument = JXDocument.create(html);

            JXNode jxNode = jxDocument.selNOne("//*[@id=\"form1\"]/table/tbody/tr[3]/td/div/table/tbody/tr/td/b/text()");
            String result = jxNode.asString();

            JXNode jxNode1 = jxDocument.selNOne("//*[@id=\"form1\"]/table/tbody/tr[1]/td/table/tbody/tr/td/span/text()");
            String update = jxNode1.asString();

            List<JXNode> jxNodes = jxDocument.selN("//*[@id=\"form1\"]/table/tbody/tr[5]/td/table/tbody/tr[2]/td/table/tbody/tr/td[not(@*) and string-length(text())>2 and string-length(text())<20]/text()");
            jxNodes.remove(0);
            List<Map<String, String>> list = new LinkedList<>();
            for (int i = 0; i < jxNodes.size(); i++) {
                JXNode site = jxNodes.get(i++);
                JXNode lowest = jxNodes.get(i);
                list.add(new HashMap<String, String>(){{
                    put("site", site.asString());
                    put("lowest", lowest.asString());
                }});
            }
            return new ResponseResult(){{
                setCode(200);
                setData(new HashMap<String, Object>(){{
                    put("result", result);
                    put("update", update);
                    put("list", list);
                }});
            }};
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(500, "服务器网络错误");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BaseException(602, "数据解析出错");
        }
    }
}
