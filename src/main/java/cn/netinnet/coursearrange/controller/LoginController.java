package cn.netinnet.coursearrange.controller;

import cn.netinnet.coursearrange.authentication.JWTUtil;
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

    @GetMapping("/login")
    public ModelAndView gotoLogin() {
        return new ModelAndView("login");
    }


    @GetMapping("/logout")
    public ModelAndView logout() {
        return gotoLogin();
    }


    @GetMapping("/index")
    public ModelAndView index(String type) {
        ModelAndView modelAndView = new ModelAndView("login");
        if (type.equals("admin")) {
            modelAndView = new ModelAndView("index");
        } else if (type.equals("student")) {
            modelAndView = new ModelAndView("indexStu");
        } else if (type.equals("teacher")) {
            modelAndView = new ModelAndView("indexTea");
        }
        return modelAndView;
    }


    @PostMapping("/login/admin")
    public ResultModel adminLogin(@NotNull String code, @NotNull String password) {
        if (!code.equals("admin")) {
            return ResultModel.error(412, "账号错误");
        } else if (!password.equals("123456")) {
            return ResultModel.error(412, "密码错误");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1L);
        userInfo.setUserCode(code);
        userInfo.setUserName("admin");
        userInfo.setUserPassword("123456");
        userInfo.setUserType("admin");
        String token = JWTUtil.sign(userInfo);
        return ResultModel.ok(token);
    }

    @PostMapping("/login/student")
    public ResultModel studentLogin(@NotNull String code, @NotNull String password) {
        NinStudent ninStudent = ninStudentService.verify(code, password);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(ninStudent.getId());
        userInfo.setUserCode(ninStudent.getStudentCode());
        userInfo.setUserName(ninStudent.getStudentName());
        userInfo.setUserPassword(ninStudent.getStudentPassword());
        userInfo.setUserType("student");
        String token = JWTUtil.sign(userInfo);
        return ResultModel.ok(token);
    }

    @PostMapping("/login/teacher")
    public ResultModel teacherLogin(@NotNull String code, @NotNull String password) {

        NinTeacher ninTeacher = ninTeacherService.verify(code, password);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(ninTeacher.getId());
        userInfo.setUserCode(ninTeacher.getTeacherCode());
        userInfo.setUserName(ninTeacher.getTeacherName());
        userInfo.setUserPassword(ninTeacher.getTeacherPassword());
        userInfo.setUserType("teacher");
        String token = JWTUtil.sign(userInfo);
        return ResultModel.ok(token);
    }

}
