package cn.wecuit.backen.services;

import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/24 7:18
 * @Version 1.0
 **/
public interface FileService {
    Map<String, Object> scanAllFile(String path, int start, int end);
}
