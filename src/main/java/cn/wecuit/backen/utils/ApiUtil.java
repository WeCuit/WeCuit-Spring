package cn.wecuit.backen.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiUtil {
    /**
     *
     * @param path [/subPkg/service/action/k1/v1]
     * @return {
     *     subPkg: String,
     *     service: String,
     *     action: String,
     *     params: Map
     * }
     */
    public static List<Map<String, String>> parseURI(String path){
        path = path.replaceAll("(/{2,})", "/").substring(1);

        String[] split = path.split("/");

        if(split.length < 3)return null;
        Map<String, String> parsedURI = new HashMap<>();

        parsedURI.put("subPkg", split[0].toLowerCase());
        parsedURI.put("service", split[1]);
        parsedURI.put("action", split[2]);

        // Handle param
        Map<String, String> params = new HashMap<>();
        for (int i = 3; i < split.length; i += 2) {
            String key = split[i];
            String value = (i + 1 < split.length)?split[i+1]:null;
            params.put(key, value);
        }

        List<Map<String, String>> maps = new ArrayList<>();
        maps.add(parsedURI);
        maps.add(params);
        return maps;
    }

    public static Map<String, String> getParameter(Map<String, String[]> parameterMap) {
        Map<String, String> parameter = new HashMap<>();
        parameterMap.forEach((k, v)->{
            parameter.put(k,v[0]);
        });
        return parameter;
    }
}
