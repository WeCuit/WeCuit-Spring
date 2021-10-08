package cn.wecuit.robot.services;

import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/10/8 12:03
 * @Version 1.0
 **/
public interface RbPictureService {
    boolean add(String imgId, Map<String, Object> info, String level);
    boolean increCntByLevel(int level);
}
