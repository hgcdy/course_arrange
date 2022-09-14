package cn.netinnet.coursearrange.authentication;

import cn.netinnet.coursearrange.entity.UserInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ShiroRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("权限验证");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        String userType = userInfo.getUserType();

        Set<String> roleSet = new HashSet<>();
        roleSet.add(userType);
        simpleAuthorizationInfo.setRoles(roleSet);

        // 获取用户权限集(查询角色权限表)
        List<String> rolePermission = Collections.emptyList();

        Set<String> permissionSet = rolePermission.stream()
                // 过滤空字符的权限
                .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        simpleAuthorizationInfo.setStringPermissions(permissionSet);

        System.out.println("权限校验成功");

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        System.out.println("身份验证");
        //从token中获取信息
        String token = ((JWTToken) authenticationToken).getToken();

        if (StringUtils.isBlank(token)) {
            throw new IncorrectCredentialsException("token为空，未通过认证");
        }
        if (!JWTUtil.verify(token)) {
            throw new IncorrectCredentialsException("token校验不通过");
        }
        UserInfo userInfo = JWTUtil.getUserInfo(token);

        if (userInfo == null) {
            throw new IncorrectCredentialsException("获取认证的用户信息为null");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userInfo, token, getName());

        System.out.println("身份验证成功");
        return info;
    }

}
