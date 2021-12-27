package cn.wecuit.backen.utils;

/**
 * @Author jiyec
 * @Date 2021/10/7 20:54
 * @Version 1.0
 **/
/*
 * @desc:提供非SPRING管理类调用管理类的功能
 * 注意在服务启动的时候进行import,apllication中引入
 */
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;
    @Override

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        if(SpringUtil.applicationContext == null){
            SpringUtil.applicationContext  = applicationContext;
        }
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
    //通过name,以及Clazz返回指定的Value
    public static <T> T getValue(String name, Class<T> clazz){
        return getApplicationContext().getEnvironment().getProperty(name, clazz);
    }
    //通过name,以及Clazz返回指定的Value
    public static String getValue(String name){
        return getApplicationContext().getEnvironment().getProperty(name);
    }
}