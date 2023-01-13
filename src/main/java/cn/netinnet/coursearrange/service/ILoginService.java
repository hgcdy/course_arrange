package cn.netinnet.coursearrange.service;


import cn.netinnet.coursearrange.entity.UserInfo;


public interface ILoginService {

    /**
     * 登录校验
     * @param code
     * @param password
     * @param type
     * @return
     */
    UserInfo verify(String code, String password, String type);
}
