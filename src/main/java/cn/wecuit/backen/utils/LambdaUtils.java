package cn.wecuit.backen.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @Author jiyec
 * @Date 2021/5/1 20:13
 * @Version 1.0
 **/
public class LambdaUtils {
    // 工具方法
    public static <T> Consumer<T> consumerWithIndex(BiConsumer<T, Integer> consumer) {
        class Obj {
            int i;
        }
        Obj obj = new Obj();
        return t -> {
            int index = obj.i++;
            consumer.accept(t, index);
        };
    }

}
