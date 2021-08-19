package cn.wecuit.backen.controller;

import cn.wecuit.backen.bean.ResponseData;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.HexUtil;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.RSAUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/6 5:49
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Tool")
public class ToolController {

    @Resource
    HttpServletRequest request;

    @Value("${wecuit.ocr.server}")
    private String OCR_SERVER;
    @Value("${wecuit.ocr.salt}")
    private String OCR_SALT;

    /**
     * 验证码识别
     *
     * @throws Exception
     */
    @RequestMapping("/captchaDecodeV2")
    public ResponseData captchaDecodeV2Action() throws Exception {
        // 获取POST 原始数据流
        ServletInputStream is = request.getInputStream();
        if(request.getContentLength() <= 0)throw new BaseException(20500, "请求异常");

        // =======请求合法性验证START====
        int start = request.getContentLength() / 3;
        int end = request.getContentLength() / 2;
        while(end - start > 20)
            end = (start + end) / 2;

        byte[] data = new byte[request.getContentLength()];
        int read = is.read(data);
        byte[] vdata = new byte[end-start];
        if (end - start >= 0) System.arraycopy(data, start, vdata, 0, end - start);

        String hexStr = HexUtil.byte2HexStr(vdata) + OCR_SALT;

        String verifyB64 = request.getHeader("x-verify");

        String verify = RSAUtils.decryptRSAByPriKey(verifyB64);

        if(!hexStr.equals(verify))throw new BaseException(20403, "验证失败");
        // =======请求合法性验证END====

        String s = HttpUtil.doFilePost(OCR_SERVER, data);

        return new ResponseData(){{
            setCode(200);
            setData(JsonUtil.string2Obj(s, Map.class));
        }};
    }
}
