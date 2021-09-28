package cn.wecuit.backen.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.wecuit.backen.mapper.RoleMenuMapper;
import cn.wecuit.backen.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
@Slf4j
public class StpInterfaceImpl implements StpInterface {

    @Resource
    UserRoleMapper userRoleMapper;
    @Resource
    RoleMenuMapper roleMenuMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        log.info("获取权限列表");
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();
        list.add("101");
        list.add("user-add");
        list.add("user-delete");
        list.add("user-update");
        list.add("user-get");
        list.add("article-get");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        log.info("获取角色列表 {}, {}", loginId, loginType);

        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        return userRoleMapper.getRoleByUserId(Long.parseLong((String)loginId));
    }

}
