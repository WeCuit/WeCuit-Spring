package cn.wecuit.robot.provider;

import cn.wecuit.mybatis.entity.MyBatis;
import cn.wecuit.robot.RobotMain;
import cn.wecuit.robot.data.mapper.PictureMapper;
import cn.wecuit.backen.utils.HTTP.HttpUtil;
import cn.wecuit.backen.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/5/23 11:04
 * @Version 1.0
 **/
@Slf4j
public class PixivTask {
    public static void pullTask() {
        int level = (int)(Math.random()*3);
        log.info("爬取的等级：{}", level);
        int num = 10;
        String api = "http://api.yuban10703.xyz:2333/setu_v4?num=" + num + "&level=" + level;

        if(level == 3){
            log.info("leve 非法！");
            return;
        }
        try {
            log.info("获取纸片人数据");
            String jsonStr = HttpUtil.doGet(api);
            Map info = JsonUtil.string2Obj(jsonStr, Map.class);
            List<Map<String, Object>> data = (List<Map<String, Object>>) info.get("data");

            for (Map<String, Object> detail : data) {

                String original = (String) detail.get("original");
                original = original.replace("i.pximg.net", "i.pixiv.cat");

                log.info("下载纸片人");
                CloseableHttpResponse pic = HttpUtil.doGet(original, null, null, "UTF-8", null);
                InputStream content = null;
                content = pic.getEntity().getContent();
                log.info("上传纸片人");
                Image image = Contact.uploadImage(RobotMain.getBot().getAsFriend(), content);
                String imageId = image.getImageId();
                log.info("ImageId: {}", imageId);

                // 插入数据库
                SqlSession sqlSession = null;
                try{
                    sqlSession = MyBatis.getSqlSessionFactory().openSession();
                    PictureMapper pictureMapper = sqlSession.getMapper(PictureMapper.class);
                    int i = pictureMapper.addPic(imageId, JsonUtil.obj2String(detail), level);

                    if(i == 1){
                        if(1 != pictureMapper.increCntByLevel(1, level)) {
                            // 加一失败
                            int insert = pictureMapper.addMeta(level);
                            if(insert != 1)
                                log.info("异常");
                        }
                    }
                    sqlSession.commit();
                }catch (Exception e){
                    if(null != sqlSession)
                        sqlSession.rollback();
                }finally {
                    if(null != sqlSession)
                    sqlSession.close();
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}
