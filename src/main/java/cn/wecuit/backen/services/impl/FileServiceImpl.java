package cn.wecuit.backen.services.impl;

import cn.wecuit.backen.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @Author jiyec
 * @Date 2021/8/24 7:18
 * @Version 1.0
 **/
@Service
public class FileServiceImpl implements FileService {
    @Value("${wecuit.data-path}")
    private String BASE_UPLOAD_PATH;

    @Override
    public Map<String, Object> scanAllFile(String path, int start, int end) {
        List<String> fileList = new LinkedList<>();
        long count = loop(fileList, path, start, end);
        return new HashMap<String, Object>(){{
            put("list", fileList);
            put("total", count);
        }};
    }

    /**
     * 循环 获取指定目录下文件个数和文件大小
     *
     * @param path
     * @return long[fileCount, dataSize(byte)]
     */
    public long loop(List<String> fileList, String path, int start, int end) {
        File file = new File(path);
        Stack<File> stack = new Stack<>();
        stack.push(file);
        long fileCount = 0;
        while (!stack.isEmpty()) {
            File child = stack.pop();
            if (child.isDirectory()) {
                // 排除隐藏目录
                if (!child.isHidden() && !child.getName().startsWith(".")) {
                    for (File f : child.listFiles()) stack.push(f);
                }
            } else if (child.isFile()) {
                // 排除隐藏文件
                if (!child.isHidden() && !child.getName().startsWith(".")) {
                    if(fileCount >= start && fileCount <= end){
                        fileList.add(child.getAbsolutePath().substring(BASE_UPLOAD_PATH.length()));
                    }
                    fileCount += 1;
                }
            }
        }
        return fileCount;
    }
}
