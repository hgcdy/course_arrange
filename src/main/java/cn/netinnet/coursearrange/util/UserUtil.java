package cn.netinnet.coursearrange.util;

import cn.netinnet.coursearrange.entity.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class UserUtil {
    public static UserInfo getUserInfo() {
        Object userInfo = SecurityUtils.getSubject().getPrincipal();
        if (userInfo == null || !(userInfo instanceof UserInfo)) {
            return null;
        }
        return (UserInfo) userInfo;
//        return new UserInfo(){{
//            setUserId(1L);
//            setUserCode("admin");
//            setUserName("admin");
//            setUserPassword("123456");
//            setUserType("admin");
//        }};
    }

}
