package cn.wecuit.robot;

import cn.wecuit.robot.entity.*;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.Event;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author jiyec
 * @Date 2021/6/15 19:04
 * @Version 1.0
 **/
@Slf4j
public class PluginHandler {

    // 注册事件   [事件 - method]
    private static final Map<String, Method> registeredEvent = new HashMap<>();
    public static final Map<String, Object> cmd2plugin = new HashMap<>();
    // 全局调用插件 (在指令未匹配到插件时调用)   [Object[]{clazz, method}]
    public static final List<Method> cmd2plugin3 = new ArrayList<>();


    // 插件列表确保首字母大写
    private static final List<String> pluginList = new LinkedList<>();

    // 插件主指令  [指令-类]
    public static final Map<String, Class<?>> cmd2plugin1 = new HashMap<>();
    // 插件的次级指令注册为主指令  [指令 - Object[]{clazz, method}]
    public static final Map<String, Object[]> cmd2plugin2 = new HashMap<>();


    public static void register(){
        log.info("注册插件指令");

        // TODO: 更换扫描方式为 https://github.com/ronmamo/reflections
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        final String BASE_PACKAGE = "cn.wecuit.robot.plugins";
        final String RESOURCE_PATTERN = "/**/*.class";
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
        try {
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            //MetadataReader 的工厂类
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader metadataReader = readerFactory.getMetadataReader(resource);
                //扫描到的class
                String classname = metadataReader.getClassMetadata().getClassName();
                String pluginName = classname.substring(classname.indexOf("plugins.") + 8);
                if(!pluginName.contains("$")
                        && pluginName.endsWith("Plugin")
                        && !pluginName.contains("Event")
                        && !pluginName.contains("msg.Message"))
                    pluginList.add(pluginName.substring(0, pluginName.indexOf("Plugin")));

                Class<?> clazz = Class.forName(classname);

                //判断是否有指定注解
                if (clazz.isAnnotationPresent(RobotPlugin.class)) {
                    Method[] methods = clazz.getMethods();
                    Map<String, Object> cmdMap = new HashMap<>();
                    if(clazz.isAnnotationPresent(MainCmd.class)){
                        // 有主指令
                        MainCmd mainCmd = clazz.getAnnotation(MainCmd.class);
                        String keyword = mainCmd.keyword();
                        StringBuilder desc = new StringBuilder(mainCmd.desc()).append("\n");
                        cmd2plugin.put(keyword, cmdMap);
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(SubCmd.class)) {
                                SubCmd subCmd = method.getAnnotation(SubCmd.class);
                                String keyword1 = subCmd.keyword();
                                if("".equals(keyword1)){
                                    keyword1 = "全局监听";
                                    cmd2plugin3.add(method);
                                }else if(subCmd.regAsMainCmd()){
                                    // 注册为主指令
                                    cmd2plugin.put(keyword1, method);
                                }else{
                                    // 加入二级指令
                                    cmdMap.put(keyword1, method);
                                }
                                desc.append(keyword1).append(" - ").append(subCmd.desc()).append("\n");
                            }
                        }
                        cmdMap.put("?", desc.toString());
                    }else {
                        // 非指令类型插件
                        for (Method method : methods) {
                            //method.getAnnotation();
                            if (method.isAnnotationPresent(RobotEventHandle.class)) {
                                RobotEventHandle annotation = method.getAnnotation(RobotEventHandle.class);
                                registeredEvent.put(annotation.event().name(), method);
                            }
                        }
                    }
                }
            }
            log.info("事件插件数目： {}", registeredEvent.size());
            log.info("指令插件数目： {}", cmd2plugin.size());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        log.info("插件注册完毕");
    }

    public static void event(Event e){
        /*
        e.getClass().getName()       --- net.mamoe.mirai.event.events.MemberMuteEvent
        e.getClass().getSimpleName() --- MemberMuteEvent
         */
        try {
            String eventType = e.getClass().getSimpleName();
            Method method = registeredEvent.get(eventType);
            if(method != null){
                method.invoke(method.getDeclaringClass().newInstance(), e);
            }else{
                log.info("事件无匹配 - EventType: {}", eventType);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException instantiationException) {
            instantiationException.printStackTrace();
        }
    }

}
