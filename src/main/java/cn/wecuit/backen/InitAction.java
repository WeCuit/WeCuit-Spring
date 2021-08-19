package cn.wecuit.backen;

import cn.wecuit.backen.utils.FileUtil;
import cn.wecuit.backen.utils.RSAUtils;
import cn.wecuit.backen.utils.TaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @Author jiyec
 * @Date 2021/8/15 18:46
 * @Version 1.0
 **/
@Component
public class InitAction implements ApplicationRunner {
    @Value("${wecuit.rsa.pri}")
    private String RSA_PRI_KEY;
    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Resource resource = resourceLoader.getResource(RSA_PRI_KEY);
        InputStream inputStream = resource.getInputStream();
        String s = FileUtil.ReadFile(inputStream);
        RSAUtils.init(s, null);
        // TaskUtil.start();
    }
}
