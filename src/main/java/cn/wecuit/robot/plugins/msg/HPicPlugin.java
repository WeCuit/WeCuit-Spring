package cn.wecuit.robot.plugins.msg;

import cn.wecuit.mybatis.entity.MyBatis;
import cn.wecuit.robot.data.Storage;
import cn.wecuit.robot.data.mapper.PictureMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author jiyec
 * @Date 2021/5/19 10:48
 * @Version 1.0
 **/
@Slf4j
public class HPicPlugin extends MessagePluginImpl {

    // 二级指令
    @Getter
    private final Map<String, String> subCmdList = new HashMap<String, String>(){{
        put("开启", "enablePic");
        put("关闭", "disablePic");
    }};
    // 需要注册为一级指令的 指令
    @Getter
    private final Map<String, String> registerAsFirstCmd = new HashMap<String, String>(){{
        put("抽(.*?)[点丶份张幅](.*?)的?(|r18)[纸色瑟涩\uD83D\uDC0D][片图圖\uD83E\uDD2E][|人]", "randZprPic");
    }};

    // 配置
    // 群号 - 配置
    private static final Map<String, Map<String, Object>> config = new HashMap<>();
    private static Pattern zprCompile = Pattern.compile("来(.*?)[点丶份张幅](.*?)的?(|r18)[色瑟涩\uD83D\uDC0D][图圖\uD83E\uDD2E]");
    private static int maxSend = 10;
    private static boolean isR18Enable = false;

    // 数据
    private static Map<String, List<Long>> freqQueue = new HashMap<>();

    private static final Map<String, Object> pluginData = new HashMap<String, Object>(){{
        put("config", config);
    }};

    // 本插件一级指令
    @Override
    public String getMainCmd() {
        return "纸片人";
    }

    @Override
    public @NotNull String getHelp() {
        return "图片系统：\nAdmin:\n  纸片人 开启 [(放空|普通)/性感/H/所有]\n  纸片人 关闭\n\n普通用户：\n  来张色图 -- 开启状态下可用（20秒后自动撤回）";
    }

    public boolean enablePic(){
        Contact subject = event.getSubject();
        String subjectId = Long.toString(subject.getId());
        Map<String, Object> groupConfig = config.get(subjectId);

        String levelCmd = cmds.size()>0?cmds.get(0):"";

        int level;
        switch (levelCmd){
            case "性感":
                level = 1;
                break;
            case "H":
                level = 2;
                break;
            case "所有":
                level = 3;
                break;
            default:
                level = 0;
                break;
        }

        if(groupConfig != null && level != (Integer) groupConfig.get("level")){
            groupConfig.put("level", level);
            subject.sendMessage("更新成功啦");
            updatePluginData();
        }else if(groupConfig != null){
            subject.sendMessage("已经是开启状态啦");
        }else{
            config.put(subjectId, new HashMap<String, Object>(){{
                put("level", level);
                put("recallTime", 20);      // 撤回时间
                put("freqCycle", 60);       // 频率周期
                put("freqCnt", 10);         // 频率周期内限定次数
                put("maxSend", 10);         // 单次最多发送

            }});
            subject.sendMessage("开启成功啦");
            updatePluginData();
        }
        return true;
    }

    public boolean disablePic(){

        Contact subject = event.getSubject();
        String subjectId = Long.toString(subject.getId());

        if(!config.containsKey(subjectId)){
            subject.sendMessage("已经是关闭状态啦");
        }else{
            config.remove(subjectId);
            subject.sendMessage("关闭成功啦");
            updatePluginData();
        }
        return true;
    }

    public boolean randZprPic(){
        Contact subject = event.getSubject();
        String subjectId = Long.toString(subject.getId());
        String msg = event.getMessage().contentToString();
        Map<String, Object> groupConfig = config.get(subjectId);

        // 无配置
        if(null == groupConfig){
            subject.sendMessage(Storage.name + "放产假了~(>_<。)＼");
            return true;
        }
        // 有配置
        Integer levelInt = (Integer) groupConfig.get("level");

        Matcher matcher = zprCompile.matcher(msg);
        String numStr = "1", tagStr="", levelStr="";
        if(matcher.find()){
            numStr = matcher.group(1).replaceAll("[^0-9-]", "");
            numStr = numStr.length()==0?"1":numStr;
            tagStr = matcher.group(2);
            levelStr = matcher.group(3);
        }
        int num = Integer.parseInt(numStr);

        // 数量检测
        if(num <= 0){
            subject.sendMessage("¿¿¿你这是要向我输出吗∑( 口 ||");
            return true;
        }else if(num > maxSend){
            subject.sendMessage("要这么多你怎么不冲死呢?");
            return true;
        }

        // 频率检测
        if(0 == getLastNum(subjectId, num)){
            subject.sendMessage("需求大于产出 (≧ω≦)");
            return true;
        }

        // r18检测
        if("r18".equals(levelStr)){
            if(isR18Enable)
                levelInt = 2;
            else{
                subject.sendMessage("没有,爪巴");
                return true;
            }
        }

        String[] levelArr = {"普通", "性感", "H", "全部"};
        String level;
        if(levelInt != 3)
            level = Integer.toString(levelInt);
        else
            level = "_";

        // String imgXml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
        //         "<msg serviceID=\"5\" templateID=\"12345\" action=\"\" brief=\"纸片人\" sourceMsgId=\"0\" url=\"\" flag=\"0\" adverSign=\"0\"\n" +
        //         "     multiMsgFlag=\"0\">\n" +
        //         "    <item layout=\"0\">\n" +
        //         "        <image uuid=\"#imageId#.png\" md5=\"#imageId#\" GroupFiledid=\"0\"\n" +
        //         "               filesize=\"81322\" local_path=\"#imageId#.png\" minWidth=\"200\" minHeight=\"200\"\n" +
        //         "               maxWidth=\"500\" maxHeight=\"1000\"/>\n" +
        //         "    </item>\n" +
        //         "    <source name=\"#title#(id:#artwork# author:#author#)\" icon=\"\" action=\"\" appid=\"-1\"/>\n" +
        //         "</msg>";

        log.info("获取纸片人数据");

        try (SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()){
            PictureMapper pictureMapper = sqlSession.getMapper(PictureMapper.class);

            StringBuilder picStr = new StringBuilder();
            for (int i = 0; i < num; i++) {

                Integer j = pictureMapper.queryPosBylevel(level);
                if(j==null){
                    subject.sendMessage(levelArr[levelInt] + "分类下没有图片╮(╯▽╰)╭");
                    return true;
                }
                Map<String, String> picture = pictureMapper.getByPosLevel(level, j);
                String imageId = picture.get("id");
                picStr.append("[mirai:image:").append(imageId).append("]");
                sqlSession.commit();
            }

            // Map<String, Object> detail = JsonUtil.string2Obj(picture.get("info"), Map.class);
            // int artwork = (int)detail.get("artwork");
            // String title = (String)detail.get("title");
            // String author = (String)detail.get("author");
            // [mirai:image:{9B077392-DA4F-D5E8-F16A-C4304DDCF819}.gif]
            MessageChain picMsg = MiraiCode.deserializeMiraiCode(picStr.toString());
            // imageId = imageId.replaceAll("\\{|}|-|\\.jpg|\\.png", "");

            // imgXml = imgXml
            //         .replace("#title#", title)
            //         .replace("#artwork#", Integer.toString(artwork))
            //         .replace("#author#", author)
            //         .replace("#imageId#", imageId);
            MessageReceipt messageReceipt = subject.sendMessage(picMsg);

            new MessageRecall(messageReceipt, (int)groupConfig.get("recallTime")).start();  // 撤回
        }

        return true;
    }

    /**
     * 根据发送频率获取实际可发送图片的数量
     *
     * @param subjectId 主体编号
     * @param num       申请发送的图片数量
     * @return          实际可发送的图片数量
     */
    private int getLastNum(String subjectId, int num){
        Map<String, Object> groupConfig = config.get(subjectId);

        int freqCycle = (int) groupConfig.get("freqCycle");
        Integer freqCnt = (Integer) groupConfig.get("freqCnt");

        // 获取发送记录
        List<Long> longs = freqQueue.get(subjectId);

        // 记录为空
        if(null == longs) {
            freqQueue.put(subjectId, new ArrayList<Long>(){{
                add(System.currentTimeMillis());
            }});
            return num;
        }

        // 筛选周期内的时间
        longs = longs.stream().filter(t->t >= System.currentTimeMillis() - freqCycle * 1000).collect(Collectors.toList());
        freqQueue.put(subjectId, longs);

        int cnt = freqCnt - longs.size();
        
        if(cnt > 0) {
            for (int i = 0; i < cnt; i++) {
                longs.add(System.currentTimeMillis());
            }
        }

        return cnt;
    }

    public void updatePluginData(){
        updatePluginData(pluginData);
    }

    // 初始化插件数据[从外部到内部]
    public void initPluginData(Map<String, Object> config){
        config.remove("enabled");
        HPicPlugin.config.putAll((Map<String, Map<String, Object>>) config.get("config"));  // 置入
    }

    @Override
    public List<String> getGlobalCmd() {
        return null;
    }
}