package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.domain.UserInfo;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherMapper;
import cn.netinnet.coursearrange.service.ILoginService;
import cn.netinnet.coursearrange.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements ILoginService{

    @Autowired
    private NinTeacherMapper ninTeacherMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;

    @Override
    public UserInfo verify(String code, String password, String type) {
        Long oldId = null;
        String oldCode = null;
        String oldPassword = null;
        if (UserTypeEnum.ADMIN.getName().equals(type)) {
            if (code.equals(ApplicationConstant.ADMIN_CODE)) {
                oldId = ApplicationConstant.ADMIN_ID;
                oldCode = code;
                oldPassword = ApplicationConstant.ADMIN_PASSWORD;
            }
        } else if(UserTypeEnum.TEACHER.getName().equals(type)) {
            NinTeacher ninTeacher = ninTeacherMapper.selectOne(new QueryWrapper<>(new NinTeacher() {{
                setTeacherCode(code);
            }}));
            if (ninTeacher != null) {
                oldId = ninTeacher.getId();
                oldCode = ninTeacher.getTeacherCode();
                oldPassword = ninTeacher.getTeacherPassword();
            }
        } else if (UserTypeEnum.STUDENT.getName().equals(type)) {
            NinStudent ninStudent = ninStudentMapper.selectOne(new QueryWrapper<>(new NinStudent() {{
                setStudentCode(code);
            }}));
            if (ninStudent != null) {
                oldId = ninStudent.getId();
                oldCode = ninStudent.getStudentCode();
                oldPassword = ninStudent.getStudentPassword();
            }
        }
        if (oldCode == null) {
            throw new ServiceException(412, "账号不存在");
        }
        if (!oldPassword.equals(MD5.getMD5Encode(password))) {
            throw new ServiceException(412, "密码错误");
        }
        UserInfo userInfo = new UserInfo(oldId, oldCode, oldPassword, type);
        return userInfo;
    }
}
