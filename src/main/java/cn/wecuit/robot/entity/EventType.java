package cn.wecuit.robot.entity;

/**
 * @Author jiyec
 * @Date 2021/6/15 21:29
 * @Version 1.0
 **/
public enum EventType {
    Quit(),
    NudgeEvent,
    MemberLeaveEvent,
    MessageEvent,
    GroupMessageEvent("群消息"),
    GroupTempMessageEvent("群临时消息"),
    UserMessageEvent,
    FriendMessageEvent("好友消息"),
    MemberJoinRequestEvent;
    EventType() {

    }
    private String message;
    EventType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
