package cn.wecuit.backen;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author jiyec
 * @Date 2021/9/28 12:35
 * @Version 1.0
 **/
public class PasswordTests {
    @Test
    public void encode(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encode = passwordEncoder.encode("12345678");
        System.out.println(encode);
        passwordEncoder.matches("12345678", encode);
    }
}
