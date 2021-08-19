package cn.wecuit.robot.entity;


import java.util.Date;

/**
 * @Author jiyec
 * @Date 2021/5/8 14:28
 * @Version 1.0
 **/
public class QQUser {
    // id
    private long id;
    // 戳的次数
    private int nudgeCount = 0;

    // 不理睬结束时间
    private long ignoreEndTime = 0L;
    // 最后一次戳的时间
    private long lastNudgeTime = 0L;

    public QQUser(){

    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public boolean isIgnored(){
        long now = new Date().getTime() / 1000;

        return now <= ignoreEndTime;
    }

    /**
     *
     * @param ignoreTime 忽略的时间[秒]
     * @return -1忽略本次操作 | int 当前被戳次数
     */
    public int addNudgeCount(int ignoreTime){
        long now = new Date().getTime() / 1000;

        if(now - lastNudgeTime > ignoreTime){
            // 最后一次戳是60秒前
            nudgeCount = 0;
        }

        lastNudgeTime = now;
        // 还在不理睬时间内
        if(now <= ignoreEndTime)
            return -1;

        int res = this.nudgeCount;

        this.nudgeCount++;
        if(nudgeCount > 3){
            // 设置不理睬时间
            ignoreEndTime = now + ignoreTime;
            // 次数置零
            nudgeCount = 0;
        }
        return res;
    }


    public long getIgnoreEndTime() {
        return ignoreEndTime;
    }

    public void setIgnoreEndTime(long ignoreEndTime) {
        this.ignoreEndTime = ignoreEndTime;
    }
}
