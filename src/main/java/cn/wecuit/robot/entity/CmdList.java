package cn.wecuit.robot.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author jiyec
 * @Date 2021/10/21 17:32
 * @Version 1.0
 **/
public class CmdList extends LinkedList<String> {
    public CmdList(List<String> asList) {
        super(asList);
    }
}
