package cn.wecuit.robot.data;

import net.mamoe.mirai.message.MessageReceipt;

import java.util.*;

/**
 * @Author jiyec
 * @Date 2021/5/8 14:44
 * @Version 1.0
 **/

public class Storage {
    private static final Map<String, Long> ignoreMap = new HashMap<>();
    public static final List<String> adminList = new ArrayList<String>(){{
        add("1690127128");
    }};
    public static final String name = "大小姐";
    private static final Map<Long, List<MessageReceipt>> oldMessage = new HashMap<>();

    public static void addOldMessage(long id, MessageReceipt receipt){
        List<MessageReceipt> messageReceipts = oldMessage.get(id);
        if(messageReceipts == null){
            messageReceipts = new LinkedList<>();
            oldMessage.put(id, messageReceipts);
        }

        // 容量为10
        if(messageReceipts.size()>10)messageReceipts.remove(0);

        messageReceipts.add(receipt);
    }
    public static MessageReceipt getMessage(long id){
        List<MessageReceipt> messageReceipts = oldMessage.get(id);
        if(messageReceipts != null && messageReceipts.size()>0){
            MessageReceipt receipt = messageReceipts.get(messageReceipts.size() - 1);
            messageReceipts.remove(messageReceipts.size() - 1);
            return receipt;
        }else{
            return null;
        }

    }

    public static void addIgnore(String key, long value){
        ignoreMap.put(key, value);
    }
    public static void delIgnore(String key){
        ignoreMap.remove(key);
    }
    public static Long getIgnore(String key){
        return ignoreMap.get(key);
    }

}
