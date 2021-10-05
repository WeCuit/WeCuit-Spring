package cn.wecuit.backen.api.v3;

import cn.wecuit.backen.entity.MiniType;
import cn.wecuit.backen.mapper.SubMapper;
import cn.wecuit.backen.mapper.AdminUserMapper;
import cn.wecuit.backen.exception.BaseException;
import cn.wecuit.backen.services.MiniService;
import cn.wecuit.backen.utils.HexUtil;
import cn.wecuit.backen.utils.RSAUtils;
import cn.wecuit.mybatis.entity.MyBatis;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyec
 * @Date 2021/8/9 6:25
 * @Version 1.0
 **/
@RestController
public class SubController {

    @Resource
    HttpServletRequest request;

    @Resource
    MiniService miniService;

    protected final String[] client = {"wx", "qq"};

    public Map<String, Object> getTemplateIdListAction() throws IOException {

        MiniType type = miniService.getMiniType(request);

        try (SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()) {
            List<HashMap<String, Object>> list = sqlSession.selectList("cn.wecuit.backen.sub.selectTplList", type.name());

            return new HashMap<String, Object>() {{
                put("code", 200);
                put("data", list);
            }};
        }

    }

    public Map<String, Object> getStatusV2Action() throws NoSuchAlgorithmException, IOException {

        String openid = request.getParameter("openid");
        String sign = request.getParameter("sign");

        String path = (request.getServletPath() + ((null == request.getPathInfo())?"":request.getPathInfo()) + "/").substring(4);
        String s = genQuerySign(path, openid);

        if(sign == null || !sign.equals(s))throw new BaseException(403, "非法请求");


        MiniType type = miniService.getMiniType(request);

        try (SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()) {

            List<HashMap<String, Object>> list = sqlSession.selectList("cn.wecuit.backen.sub.subStatus",
                    new HashMap<String, String>(){{
                        put("client", type.name());
                        put("openid", openid);
                    }}
            );
            return new HashMap<String, Object>() {{
                put("code", 200);
                put("sub", list);
            }};
        }

    }

    public Map<String, Object> changeStatusV2Action() throws Exception {
        String openid = request.getParameter("openid");
        String status = request.getParameter("status");
        String tplId = request.getParameter("tplId");
        String sign = request.getParameter("sign");
        String userId = request.getParameter("userId");
        String userPass = request.getParameter("userPass");

        MiniType type = miniService.getMiniType(request);

        String path = (request.getServletPath() + ((null == request.getPathInfo())?"":request.getPathInfo()) + "/").substring(4);
        String s = genQuerySign(path, openid, tplId);

        if(sign == null || !sign.equals(s))throw new BaseException(403, "非法请求");

        String pass = RSAUtils.decryptRSAByPriKey(userPass);

        if(userId.isEmpty() || pass.isEmpty())throw new BaseException(10401, "账号密码缺失");

        boolean sub_enable = false;
        if("true".equals(status))sub_enable=true;

        try(SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()){
            AdminUserMapper userMapper = sqlSession.getMapper(AdminUserMapper.class);
            SubMapper subMapper = sqlSession.getMapper(SubMapper.class);

            BigInteger uid = userMapper.queryUIdBySId(userId);
            // 用户账号数据处理
            if(uid != null){
                // 旧用户
                int i = userMapper.updateStuInfoByUId(uid, userId, userPass);
                if(i != 1)throw new BaseException(10500, "更新用户失败");
            }else{
                // 新用户
                Map<String, BigInteger> uid_t = new HashMap<>();
                int i = userMapper.addUser(uid_t, type.name(), openid, userId, userPass);
                uid = uid_t.get("uid");
                if(i!=1 || uid == null)throw new BaseException(10500, "新增用户失败");

            }

            // 订阅信息处理
            int i = subMapper.setEnable(sub_enable, uid, tplId);
            if(i == 0){
                // 影响0行，可能不存在
                int i1 = subMapper.insertSub(uid, tplId);

                if(i1 != 1)throw new BaseException(10500, "订阅信息更新失败");
            }

            sqlSession.commit();

        }

        return new HashMap<String, Object>(){{
            put("code", 2000);
            put("errMsg", "已更新");
        }};

    }

    // TODO: QQ WX分开删
    public Map<String, Object> deleteV2Action() throws NoSuchAlgorithmException, IOException {
        String openid = request.getParameter("openid");
        String sign = request.getParameter("sign");

        String path = (request.getServletPath() + ((null == request.getPathInfo())?"":request.getPathInfo()) + "/").substring(4);
        String s = genQuerySign(path, openid);

        if(sign == null || !sign.equals(s))throw new BaseException(403, "非法请求");

        try(SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()){
            AdminUserMapper userMapper = sqlSession.getMapper(AdminUserMapper.class);
            SubMapper subMapper = sqlSession.getMapper(SubMapper.class);

            MiniType type = miniService.getMiniType(request);
            BigInteger uid = userMapper.queryUIdByOpenId(type.name(), openid);
            int delete = subMapper.deleteSub(uid);

            if(delete==0)throw new BaseException(10500, "删除失败");

            sqlSession.commit();
        }

        return new HashMap<String, Object>(){{
            put("code", 200);
            put("errMsg", "已删除");
        }};
    }

    public Map<String, Object> addCntV2Action() throws NoSuchAlgorithmException, IOException {
        String openid = request.getParameter("openid");
        String tplId = request.getParameter("tplId");
        String sign = request.getParameter("sign");

        String path = (request.getServletPath() + ((null == request.getPathInfo())?"":request.getPathInfo()) + "/").substring(4);
        String s = genQuerySign(path, openid, tplId);
        if(sign == null || !sign.equals(s))throw new BaseException(403, "非法请求");

        try(SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession()){
            AdminUserMapper userMapper = sqlSession.getMapper(AdminUserMapper.class);
            SubMapper subMapper = sqlSession.getMapper(SubMapper.class);

            MiniType type = miniService.getMiniType(request);
            BigInteger uid = userMapper.queryUIdByOpenId(type.name(), openid);
            int i = subMapper.incrCnt(uid, tplId);
            if(i != 1)throw new BaseException(10500, "操作失败");

            sqlSession.commit();
        }

        return new HashMap<String, Object>(){{
            put("code", 200);
            put("errMsg", "+1");
        }};
    }

    public String genQuerySign(String path, String openid, String data) throws NoSuchAlgorithmException {
        byte[] pathData = RSAUtils.genMD5(path.getBytes(StandardCharsets.UTF_8));
        byte[] openidData = RSAUtils.genMD5(openid.getBytes(StandardCharsets.UTF_8));
        byte[] dataData = RSAUtils.genMD5(data.getBytes(StandardCharsets.UTF_8));
        String query_salt = request.getServletContext().getInitParameter("QUERY_SALT");

        String s = HexUtil.byte2HexStr(pathData) + HexUtil.byte2HexStr(openidData) + HexUtil.byte2HexStr(dataData) + query_salt;
        byte[] bytes = RSAUtils.genMD5(s.getBytes(StandardCharsets.UTF_8));

        return HexUtil.byte2HexStr(bytes);
    }
    public String genQuerySign(String path, String openid) throws NoSuchAlgorithmException {
        return genQuerySign(path, openid, "");
    };
}
