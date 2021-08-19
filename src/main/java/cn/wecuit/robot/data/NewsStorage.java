package cn.wecuit.robot.data;

import cn.wecuit.mybatis.entity.MyBatis;
import cn.wecuit.robot.data.mapper.NewsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

/**
 * @Author jiyec
 * @Date 2021/5/11 22:51
 * @Version 1.0
 **/
@Slf4j
public class NewsStorage {

    public static boolean isNewsExist(String md5){

        try(SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()){
            NewsMapper mapper = sqlSession.getMapper(NewsMapper.class);
            int i = mapper.selCnt(md5);
            return i>0;
        }
    }
    public static boolean addNews(String md5){

        try(SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()){
            NewsMapper mapper = sqlSession.getMapper(NewsMapper.class);
            int i = mapper.addNoticed(md5);
            sqlSession.commit();
            return i>0;
        }
    }
    public static void delOutDate(){
        try(SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()){
            NewsMapper mapper = sqlSession.getMapper(NewsMapper.class);
            int i = mapper.delOutDate();
            sqlSession.commit();
            log.info("删除了{}条数据", i);
        }
    }

}
