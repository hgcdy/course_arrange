package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.authentication.JWTUtil;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.constant.CacheConstant;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.domain.UserInfo;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.LoginService;
import cn.netinnet.coursearrange.util.MD5;
import cn.netinnet.coursearrange.util.RedisUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private NinTeacherMapper ninTeacherMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;

    @Override
    public Map<String, Object> verify(String code, String password, String type) {
        Long oldId = null;
        String oldName = null;
        String oldCode = null;
        String oldPassword = null;
        if (UserTypeEnum.ADMIN.getName().equals(type)) {
            if (code.equals(ApplicationConstant.ADMIN_CODE)) {
                oldId = ApplicationConstant.ADMIN_ID;
                oldName = ApplicationConstant.ADMIN_NAME;
                oldCode = ApplicationConstant.ADMIN_CODE;
                oldPassword = ApplicationConstant.ADMIN_PASSWORD;
            }
        } else if(UserTypeEnum.TEACHER.getName().equals(type)) {
            NinTeacher ninTeacher = ninTeacherMapper.selectOne(new LambdaQueryWrapper<NinTeacher>()
                    .eq(NinTeacher::getTeacherCode, code));
            if (ninTeacher != null) {
                oldId = ninTeacher.getId();
                oldName = ninTeacher.getTeacherName();
                oldCode = ninTeacher.getTeacherCode();
                oldPassword = ninTeacher.getTeacherPassword();
            }
        } else if (UserTypeEnum.STUDENT.getName().equals(type)) {
            NinStudent ninStudent = ninStudentMapper.selectOne(new LambdaQueryWrapper<NinStudent>()
                    .eq(NinStudent::getStudentCode, code));
            if (null != ninStudent) {
                oldId = ninStudent.getId();
                oldName = ninStudent.getStudentName();
                oldCode = ninStudent.getStudentCode();
                oldPassword = ninStudent.getStudentPassword();
            }
        }
        if (null == oldCode) {
            throw new ServiceException(412, "账号不存在");
        }
        if (!oldPassword.equals(MD5.getMD5Encode(password))) {
            throw new ServiceException(412, "密码错误");
        }
        UserInfo userInfo = new UserInfo(oldId, oldName, oldCode, type);

        String token = JWTUtil.sign(userInfo);
        String key = String.format(CacheConstant.LOGIN_TOKEN, userInfo.getUserId().toString());
        RedisUtil.set(key, token);

        return new HashMap<String, Object>() {{
            put("token", token);
            put("userId", userInfo.getUserId().toString());
            put("role", userInfo.getUserType());
        }};
    }

    @Override
    public void passwordVerify(String password) {
        if (password != null) {
            int length = password.length();
            if (length < 6 || length > 14) {
                throw new ServiceException(412, "密码长度为6-15");
            }
            for (int i = 0; i < length; i++) {
                char c = password.charAt(i);
                if (c < 32 || c >= 127) {
                    throw new ServiceException(412, "密码请不要包含不能识别的特殊字符");
                }
            }
        } else {
            throw new ServiceException(412, "密码为空");
        }
    }

    @Override
    public ResultModel alterPassword(String oldPassword, String newPassword) {
        passwordVerify(newPassword);

        UserInfo userInfo = UserUtil.getUserInfo();
        Long userId = userInfo.getUserId();
        String userType = userInfo.getUserType();
        String password = "";
        NinStudent ninStudent = null;
        NinTeacher ninTeacher = null;

        if (userType.equals(UserTypeEnum.STUDENT.getName())) {
            ninStudent = ninStudentMapper.selectById(userId);
            password = ninStudent.getStudentPassword();
            ninStudent.setStudentPassword(MD5.getMD5Encode(newPassword));
        } else if (userType.equals(UserTypeEnum.TEACHER.getName())){
            ninTeacher = ninTeacherMapper.selectById(userId);
            password = ninTeacher.getTeacherPassword();
            ninTeacher.setTeacherPassword(MD5.getMD5Encode(newPassword));
        }

        if (!oldPassword.equals(newPassword)) {
            if (password.equals(MD5.getMD5Encode(oldPassword))) {
                if (userType.equals(UserTypeEnum.STUDENT.getName())) {
                    ninStudentMapper.updateById(ninStudent);
                } else if (userType.equals(UserTypeEnum.TEACHER.getName())){
                    ninTeacherMapper.updateById(ninTeacher);
                }
            } else {
                return ResultModel.error(412, "旧密码验证错误");
            }
        } else {
            return ResultModel.error(412, "新密码和旧密码一致");
        }
        return ResultModel.ok();
    }

    @Override
    public String refreshToken(HttpServletRequest request) {
        String token = JWTUtil.getToken(request);
        UserInfo userInfo = JWTUtil.getUserInfo(token);
        String newToken = JWTUtil.sign(userInfo);
        String key = String.format(CacheConstant.LOGIN_TOKEN, userInfo.getUserId().toString());
        RedisUtil.set(key, newToken);
        return newToken;
    }
}
