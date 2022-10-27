package cn.netinnet.coursearrange.authentication;

import cn.netinnet.coursearrange.entity.UserInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

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
        System.out.println("-----开始权限验证-----");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //获取用户信息及用户角色类型
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        String userType = userInfo.getUserType();

        Set<String> roleSet = new HashSet<>();
        roleSet.add(userType);
        info.setRoles(roleSet);

        // 获取用户权限集(查询角色权限表)
        List<String> rolePermission = Collections.emptyList();

        Set<String> permissionSet = rolePermission.stream()
                // 过滤空字符的权限
                .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        info.setStringPermissions(permissionSet);

        System.out.println("-----权限校验成功-----");

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        System.out.println("-----开始身份验证-----");

        //从token中获取信息
        String token = ((JWTToken) authenticationToken).getToken();

        //校验是否为null或"  "或""三种情况
        // 是则返回true
        if (StringUtils.isBlank(token)) {
            throw new IncorrectCredentialsException("---token为空，未通过认证---");
        }

        if (!JWTUtil.verify(token)) {
            throw new IncorrectCredentialsException("---token校验不通过---");
        }

        UserInfo userInfo = JWTUtil.getUserInfo(token);

        //校验获取的用户信息是否为空
        if (userInfo == null) {
            throw new IncorrectCredentialsException("---获取认证的用户信息为空---");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userInfo, token, getName());

        System.out.println("-----身份验证成功-----");

        return info;
    }

}
