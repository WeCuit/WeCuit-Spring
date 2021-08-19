package cn.wecuit.backen.utils.StringUtil;

import java.util.regex.Matcher;

/**
 * @Author jiyec
 * @Date 2021/5/1 14:06
 * @Version 1.0
 **/
public interface ReplaceCallBack {
    /**
     * 将text转化为特定的字串返回
     * @param text 指定的字符串
     * @param index 替换的次序
     * @param matcher Matcher对象
     * @return
     */
    public String replace(String text, int index, Matcher matcher);
}
