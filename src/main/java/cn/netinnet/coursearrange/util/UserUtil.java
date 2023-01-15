package cn.netinnet.coursearrange.util;

import cn.netinnet.coursearrange.domain.UserInfo;
import org.apache.shiro.SecurityUtils;

public class UserUtil {
    public static UserInfo getUserInfo() {
        Object userInfo = SecurityUtils.getSubject().getPrincipal();
        if (userInfo == null || !(userInfo instanceof UserInfo)) {
            return null;
        }
        return (UserInfo) userInfo;
    }

}
