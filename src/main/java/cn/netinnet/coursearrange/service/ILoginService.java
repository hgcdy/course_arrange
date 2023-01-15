package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.domain.UserInfo;

public interface ILoginService {

    /**
     * 登录校验
     */
    UserInfo verify(String code, String password, String type);
}
