package cn.netinnet.coursearrange.domain;

import lombok.Data;

import java.io.Serializable;

@Data
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
     * 用户身份
     */
    private String UserType;

    public UserInfo() {
    }

    public UserInfo(Long userId, String userName, String userCode, String userType) {
        UserId = userId;
        UserName = userName;
        UserCode = userCode;
        UserType = userType;
    }

}
