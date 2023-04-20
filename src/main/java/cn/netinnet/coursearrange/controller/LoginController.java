package cn.netinnet.coursearrange.controller;

import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.LoginService;
import cn.netinnet.coursearrange.util.UserUtil;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("")
public class LoginController {

    @Autowired
    private LoginService loginService;

    //跳转登录页面
    @GetMapping("/login")
    public ModelAndView gotoLogin() {
        return new ModelAndView("login");
    }

    //退出，跳转登录页面
    @GetMapping("/logout")
    public ModelAndView logout() {
        return gotoLogin();
    }

    //欢迎页面
    @GetMapping("/welcome")
    public ModelAndView welcome() {return new ModelAndView("welcome");}

    //判断登录用户类型
    @GetMapping("/index")
    public ModelAndView index(String type) {
        String path = "login";
        if (type.equals(UserTypeEnum.ADMIN.getName())) {
            path = "index";
        } else if (type.equals(UserTypeEnum.STUDENT.getName())) {
            path = "indexStu";
        } else if (type.equals(UserTypeEnum.TEACHER.getName())) {
            path = "indexTea";
        }
        return new ModelAndView(path);
    }

    //登录校验
    @PostMapping("/login/{type}")
    public ResultModel login(@NotNull String code, @NotNull String password, @PathVariable String type) {
        return ResultModel.ok(loginService.verify(code, password, type));
    }

    //获取用户信息
    @GetMapping("/getUserInfo")
    public ResultModel getUserInfo() {
        return ResultModel.ok(UserUtil.getUserInfo());
    }

    //修改密码
    @PostMapping("/alterPassword")
    public ResultModel alterPassword(String oldPassword, String newPassword) {
        return loginService.alterPassword(oldPassword, newPassword);
    }

    //token刷新
    @GetMapping("refreshToken")
    public ResultModel refreshToken(HttpServletRequest request) {
        return ResultModel.ok(loginService.refreshToken(request));
    }


}
