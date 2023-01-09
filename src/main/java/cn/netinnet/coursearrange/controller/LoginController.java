package cn.netinnet.coursearrange.controller;

import cn.netinnet.coursearrange.authentication.JWTUtil;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinStudentService;
import cn.netinnet.coursearrange.service.INinTeacherService;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("")
public class LoginController {

    @Autowired
    private INinStudentService ninStudentService;
    @Autowired
    private INinTeacherService ninTeacherService;

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

    //管理员登录
    @PostMapping("/login/admin")
    public ResultModel adminLogin(@NotNull String code, @NotNull String password) {
        if (!code.equals(ApplicationConstant.ADMIN_CODE)) {
            return ResultModel.error(412, "账号错误");
        } else if (!password.equals(ApplicationConstant.ADMIN_PASSWORD)) {
            return ResultModel.error(412, "密码错误");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(ApplicationConstant.ADMIN_ID);
        userInfo.setUserCode(ApplicationConstant.ADMIN_CODE);
        userInfo.setUserName(ApplicationConstant.ADMIN_NAME);
        userInfo.setUserType(ApplicationConstant.TYPE_ADMIN);
        String token = JWTUtil.sign(userInfo);
        return ResultModel.ok(token);
    }

    //学生登录
    @PostMapping("/login/student")
    public ResultModel studentLogin(@NotNull String code, @NotNull String password) {
        NinStudent ninStudent = ninStudentService.verify(code, password);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(ninStudent.getId());
        userInfo.setUserCode(ninStudent.getStudentCode());
        userInfo.setUserName(ninStudent.getStudentName());
        userInfo.setUserType(ApplicationConstant.TYPE_STUDENT);
        String token = JWTUtil.sign(userInfo);
        return ResultModel.ok(token);
    }

    //教师登录
    @PostMapping("/login/teacher")
    public ResultModel teacherLogin(@NotNull String code, @NotNull String password) {

        NinTeacher ninTeacher = ninTeacherService.verify(code, password);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(ninTeacher.getId());
        userInfo.setUserCode(ninTeacher.getTeacherCode());
        userInfo.setUserName(ninTeacher.getTeacherName());
        userInfo.setUserType(ApplicationConstant.TYPE_TEACHER);
        String token = JWTUtil.sign(userInfo);
        return ResultModel.ok(token);
    }
}
