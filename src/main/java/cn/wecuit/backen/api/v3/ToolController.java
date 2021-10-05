package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.response.BaseResponse;
import cn.wecuit.backen.response.ResponseResult;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.HexUtil;
import cn.wecuit.backen.utils.JsonUtil;
import cn.wecuit.backen.utils.QrcodeGenerator;
import cn.wecuit.backen.utils.RSAUtils;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/6 5:49
 * @Version 1.0
 **/
@RestController
@RequestMapping("/Tool")
@BaseResponse
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
    @PostMapping("/captchaDecodeV2")
    public Map captchaDecodeV2Action() throws Exception {
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

        return JsonUtil.string2Obj(s, Map.class);
    }

    @GetMapping("/qrCode")
    public Map<String, Object> str2qr(@RequestParam String str, @RequestParam(required = false, defaultValue = "100") int width, @RequestParam(required = false, defaultValue = "100") int height){
        String qrstr = null;
        try {
            byte[] qrCodeImage = QrcodeGenerator.getQRCodeImage(str, width, height);
            qrstr = Base64.getEncoder().encodeToString(qrCodeImage);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String finalQrstr = qrstr;
        return new HashMap<String, Object>(){{
            put("img", "data:image/png;base64," + finalQrstr);
        }};
    }
}
