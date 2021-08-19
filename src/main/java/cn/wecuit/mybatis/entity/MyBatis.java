package cn.wecuit.mybatis.entity;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @Author jiyec
 * @Date 2021/5/10 21:40
 * @Version 1.0
 **/
public class MyBatis {
    private static SqlSessionFactory sqlSessionFactory;

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        MyBatis.sqlSessionFactory = sqlSessionFactory;
    }

}
