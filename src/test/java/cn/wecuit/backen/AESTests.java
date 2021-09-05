package cn.wecuit.backen;

import cn.wecuit.backen.utils.AESUtil;
import org.junit.jupiter.api.Test;

/**
 * @Author jiyec
 * @Date 2021/9/5 10:54
 * @Version 1.0
 **/
public class AESTests {
    @Test
    public void decrypt() throws Exception {
        System.out.println(AESUtil.Decrypt("dlBxQd7yBoWSSoO5W6Fhxg==", "eSEdSfdED==WGDHA"));
    }
}
