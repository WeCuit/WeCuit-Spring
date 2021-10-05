package cn.wecuit.backen.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.core.io.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Hashtable;

/**
 * @Author jiyec
 * @Date 2021/10/5 10:06
 * @Version 1.0
 **/
public class QrcodeGenerator {
    /**
     * 保存二维码图片
     *
     * @param text 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param filePath 文件保存路径
     * @throws WriterException
     * @throws IOException
     */
    public static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * 返回二维码字节数组
     * @param text
     * @param width
     * @param height
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriterCustom qrCodeWriter = new QRCodeWriterCustom();
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);//去除多余白边
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        //// LOGO
        //BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ////获取画笔
        //Graphics2D graphics = bufferedImage.createGraphics();
        ////读取logo图片
        //InputStream logoStream = QrcodeGenerator.class.getResourceAsStream("/static/logo.png");
        //BufferedImage logo = ImageIO.read(logoStream);
        ////设置二维码大小，太大了会覆盖二维码，此处为20%
        //int logoWidth = logo.getWidth() > bufferedImage.getWidth()*2 /10 ? (bufferedImage.getWidth()*2 /10) : logo.getWidth();
        //int logoHeight = logo.getHeight() > bufferedImage.getHeight()*2 /10 ? (bufferedImage.getHeight()*2 /10) : logo.getHeight();
        ////设置logo图片放置的位置，中心
        //int x = (bufferedImage.getWidth() - logoWidth) / 2;
        //int y = (bufferedImage.getHeight() - logoHeight) / 2;
        ////开始合并并绘制图片
        //graphics.drawImage(logo,x,y,logoWidth,logoHeight,null);
        //graphics.drawRoundRect(x,y,logoWidth,logoHeight,15,15);
        ////logob边框大小
        //graphics.setStroke(new BasicStroke(2));
        ////logo边框颜色
        //graphics.setColor(Color.WHITE);
        //graphics.drawRect(x,y,logoWidth,logoHeight);
        //graphics.dispose();
        //logo.flush();
        //bufferedImage.flush();
        //ImageIO.write(bufferedImage, "PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }

    public static void main(String[] args) {
            //byte[]a = getQRCodeImage("http://www.baidu.com", 350, 350);
            //BASE64Encoder encoder = new BASE64Encoder();
            //String png_base64 = encoder.encodeBuffer(a).trim();//转换成base64串
            //png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
            //System.out.println("值为："+"data:image/jpg;base64,"+png_base64);

    }
}
