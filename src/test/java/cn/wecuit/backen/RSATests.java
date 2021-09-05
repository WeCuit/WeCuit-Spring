package cn.wecuit.backen;

import cn.wecuit.backen.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author jiyec
 * @Date 2021/9/5 8:57
 * @Version 1.0
 **/
@SpringBootTest
@Slf4j
public class RSATests {
    @Test
    public void encrypt() throws Exception {
        String s = RSAUtils.encryptRSAByPubKey("12345678");
        log.info("encrypt: {}", s);
    }
}
