package cn.wecuit.backen.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/2 15:31
 * @Version 1.0
 **/
public class CardUtil {
    public static Map<String, String> genSign(Map<String, String> param, String type) throws NoSuchAlgorithmException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yMdHms");

        String time = df.format(now);
        String key = "ok15we1@oid8x5afd@";
        String st = "";

        switch(type){
            case "DealRec":
                st =    param.get("AccNum") + "|" + param.get("BeginDate") + "|" +
                        param.get("Count") + "|" + param.get("EndDate") + "|" +
                        param.get("RecNum") + "|" + time + "|" +
                        param.get("Type") + "|" + param.get("ViceAccNum") + "|" +
                        param.get("WalletNum") + "|" + key;
                break;
            case "AccBulletin":
                break;
            case "AccAuth":
            case "AccWallet":
            case "QRCode":
                st = param.get("AccNum") + "|" + time + "|" + key;
                break;
            case "QRCodeInfo":
                st = param.get("QRCode") + "|" + time + "|" + key;
                break;
        }

        byte[] md5s = MessageDigest.getInstance("md5").digest(st.getBytes(StandardCharsets.UTF_8));
        String md5 = HexUtil.byte2HexStr(md5s);
        return new HashMap<String, String>(){{
            put("Time", time);
            put("Sign", md5);
        }};
    }
}
