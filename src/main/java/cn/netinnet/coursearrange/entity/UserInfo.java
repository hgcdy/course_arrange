package cn.netinnet.coursearrange.entity;

import java.io.Serializable;

public class UserInfo implements Serializable {

    /**
     * 用户id
     */
    private Long UserId;

    /**
     * 用户名字
     */
    private String UserName;

    /**
     * 用户账号（编号）
     */
    private String UserCode;

    /**
     * 用户密码
     */
    private String UserPassword;

    /**
     * 用户身份
     */
    private String UserType;

    public UserInfo() {
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

}
