package cn.wecuit.backen.entity.XSC;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author jiyec
 * @Date 2021/11/2 19:42
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseScore {
    private int id;
    private String stuId;
    private String stuName;
    private double avgScore;
    private String rank;
    private String className;
    private String year;
    private String major;
    private String college;
    private String hasFail;
    private String semester;
}
