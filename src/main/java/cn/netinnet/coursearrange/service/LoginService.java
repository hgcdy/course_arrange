package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.domain.UserInfo;
import cn.netinnet.coursearrange.model.ResultModel;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface LoginService {
    /**
     * 登录校验
     */
    Map<String, Object> verify(String code, String password, String type);
    /**
     * 校验密码是否合法
     */
    void passwordVerify(String password);
    /**
     * 修改密码
     */
    ResultModel alterPassword(String oldPassword, String newPassword);
    /**
     * token刷新
     * @param request
     * @return
     */
    String refreshToken(HttpServletRequest request);
}
