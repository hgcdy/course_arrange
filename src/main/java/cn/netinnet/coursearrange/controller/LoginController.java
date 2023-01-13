package cn.netinnet.coursearrange.controller;

import cn.netinnet.coursearrange.authentication.JWTUtil;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.ILoginService;
import cn.netinnet.coursearrange.service.INinStudentService;
import cn.netinnet.coursearrange.service.INinTeacherService;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("")
public class LoginController {

    @Autowired
    private ILoginService loginService;


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
        ModelAndView modelAndView = null;
        if (type.equals(ApplicationConstant.TYPE_ADMIN)) {
            modelAndView = new ModelAndView("index");
        } else if (type.equals(ApplicationConstant.TYPE_STUDENT)) {
            modelAndView = new ModelAndView("viewStu/indexStu");
        } else if (type.equals(ApplicationConstant.TYPE_TEACHER)) {
            modelAndView = new ModelAndView("viewTea/indexTea");
        } else {
            modelAndView = new ModelAndView("login");
        }
        return modelAndView;
    }

    @PostMapping("/login/{type}")
    public ResultModel login(@NotNull String code, @NotNull String password, @PathVariable String type) {
        UserInfo userInfo = loginService.verify(code, password, type);
        String token = JWTUtil.sign(userInfo);
        return ResultModel.ok(token);
    }
}
