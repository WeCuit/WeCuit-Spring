package cn.wecuit.robot.data;

import cn.wecuit.backen.utils.SpringUtil;
import cn.wecuit.robot.services.RbNewsService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author jiyec
 * @Date 2021/5/11 22:51
 * @Version 1.0
 **/
@Slf4j
public class NewsStorage {

    public static boolean isNewsExist(String md5){
        RbNewsService newsService = SpringUtil.getBean(RbNewsService.class);
        return newsService.isNewsExist(md5);
    }
    public static boolean addNews(String md5){

        RbNewsService newsService = SpringUtil.getBean(RbNewsService.class);
        return newsService.addNoticed(md5);
    }
    public static void delOutDate(){

        RbNewsService newsService = SpringUtil.getBean(RbNewsService.class);
        int i = newsService.delOutDated();
        log.info("删除了{}条数据", i);
    }

}
