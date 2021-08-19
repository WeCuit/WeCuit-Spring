package cn.wecuit.backen.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyec
 * @Date 2021/5/1 14:05
 * @Version 1.0
 **/
public class StringUtils {

    /**
     * 将String中的所有regex匹配的字串全部替换掉
     * @param string 代替换的字符串
     * @param regex 替换查找的正则表达式
     * @param replacement 替换函数
     * @return
     */
    public static String replaceAll(String string, String regex, ReplaceCallBack replacement) {
        return replaceAll(string, Pattern.compile(regex), replacement);
    }

    /**
     * 将String中的所有pattern匹配的字串替换掉
     * @param string 代替换的字符串
     * @param pattern 替换查找的正则表达式对象
     * @param replacement 替换函数
     * @return
     */
    public static String replaceAll(String string, Pattern pattern, ReplaceCallBack replacement) {
        if (string == null) {
            return null;
        }
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            StringBuffer sb = new StringBuffer();
            int index = 0;
            do {
                m.appendReplacement(sb, replacement.replace(m.group(0), index++, m));
            } while (m.find());
            m.appendTail(sb);
            return sb.toString();
        }
        return string;
    }

    /**
     * 将String中的regex第一次匹配的字串替换掉
     * @param string 代替换的字符串
     * @param regex 替换查找的正则表达式
     * @param replacement 替换函数
     * @return
     */
    public static String replaceFirst(String string, String regex, ReplaceCallBack replacement) {
        return replaceFirst(string, Pattern.compile(regex), replacement);
    }

    /**
     * 将String中的pattern第一次匹配的字串替换掉
     * @param string 代替换的字符串
     * @param pattern 替换查找的正则表达式对象
     * @param replacement 替换函数
     * @return
     */
    public static String replaceFirst(String string, Pattern pattern, ReplaceCallBack replacement) {
        if (string == null) {
            return null;
        }
        Matcher m = pattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        if (m.find()) {
            m.appendReplacement(sb, replacement.replace(m.group(0), 0, m));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 中文GBK编码转UTF-8
     * 来源未知
     *
     * @param gbkStr
     * @return
     */
    public static String getUTF8StringFromGBKString(String gbkStr) {
        try {
            return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError();
        }
    }

    public static byte[] getUTF8BytesFromGBKString(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }
}
