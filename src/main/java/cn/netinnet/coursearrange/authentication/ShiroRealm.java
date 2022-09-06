package cn.netinnet.coursearrange.authentication;

import cn.netinnet.coursearrange.entity.UserInfo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


public class ShiroRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("权限验证");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        UserInfo primaryPrincipal = (UserInfo) principalCollection.getPrimaryPrincipal();
//        UserInfo userInfo = UserUtil.getUserInfo();
        /*查询用户的权限*/
        String role = primaryPrincipal.getUserType();
        info.addRole(role);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        System.out.println("身份验证");
        //从token中获取信息
        String token = ((JWTToken) authenticationToken).getToken();
//        String token = String.valueOf(authenticationToken.getCredentials());
        if (!JWTUtil.verify(token)) {
            throw new IncorrectCredentialsException("token校验不通过");
        }
        UserInfo userInfo = JWTUtil.getUserInfo(token);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userInfo, token, getName());
        return info;
    }

}
