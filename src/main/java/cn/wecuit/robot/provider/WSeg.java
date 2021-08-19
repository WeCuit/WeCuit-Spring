package cn.wecuit.robot.provider;

// import org.apdplat.word.WordSegmenter;
// import org.apdplat.word.segmentation.Segmentation;
// import org.apdplat.word.segmentation.SegmentationAlgorithm;
// import org.apdplat.word.segmentation.SegmentationFactory;
// import org.apdplat.word.segmentation.Word;

import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.JsonUtil;
import lombok.Setter;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/16 12:29
 * @Version 1.0
 **/
public class WSeg {
    @Setter
    private static String token;
    private static String api = "http://comdo.hanlp.com/hanlp/v1/keyword/extract";

    // private static Segmentation segmentation = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaxNgramScore);
    public static List<String> seg(String msg){
        // return segmentation.seg(msg);
        Map<String, String> header = new HashMap<String, String>(){{
            put("token", token);
        }};
        Map<String, String> param = new HashMap<String, String>(){{
            put("text", msg);
            put("size", "3");
        }};
        List<String> result = new ArrayList<>();
        try {
            String s = HttpUtil.doPost(api, param, header);
            Map map = JsonUtil.string2Obj(s, Map.class);
            int code = (int) map.get("code");
            if(code == 0){
                List<Map<String, String>> data = (List<Map<String, String>>) map.get("data");
                data.forEach(m-> result.add(m.get("word")));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
