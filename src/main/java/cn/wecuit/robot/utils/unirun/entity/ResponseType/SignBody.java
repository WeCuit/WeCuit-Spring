package cn.wecuit.robot.utils.unirun.entity.ResponseType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API: v1/clubactivity/signInOrSignBack
 *
 * @Author jiyec
 * @Date 2021/11/15 18:19
 * @Version 1.0
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignBody {
    private Long activityId;
    private String latitude;
    private String longitude;
    private String signType;
    private Long studentId;
}
