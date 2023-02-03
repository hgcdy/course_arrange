package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.TeacherBo;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.domain.UserInfo;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinTeacherCourseMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinTeacherService;
import cn.netinnet.coursearrange.service.LoginService;
import cn.netinnet.coursearrange.util.MD5;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Service
public class NinTeacherServiceImpl extends ServiceImpl<NinTeacherMapper, NinTeacher> implements INinTeacherService {
    @Autowired
    private NinTeacherMapper ninTeacherMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private LoginService loginService;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, String teacherName) {
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(page, size);
        List<TeacherBo> list = ninTeacherMapper.getSelectList(teacherName);
        PageInfo<TeacherBo> pageInfo = new PageInfo<>(list);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public boolean addSingle(NinTeacher ninTeacher) {
        int count = count(new LambdaQueryWrapper<NinTeacher>().eq(NinTeacher::getTeacherCode, ninTeacher.getTeacherCode()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }

        String password = ninTeacher.getTeacherPassword();
        loginService.passwordVerify(password);
        ninTeacher.setTeacherPassword(MD5.getMD5Encode(password));

        return save(ninTeacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delById(Long id) {
        //删除教师-课程表
        ninTeacherCourseMapper.delete(new LambdaQueryWrapper<NinTeacherCourse>().eq(NinTeacherCourse::getTeacherId, id));
        return removeById(id);
    }

    @Override
    public int alterSingle(NinTeacher ninTeacher) {
        //名字，编号重名验证
        int count = count(new LambdaQueryWrapper<NinTeacher>()
                .eq(NinTeacher::getTeacherCode, ninTeacher.getTeacherCode())
                .ne(NinTeacher::getId, ninTeacher.getId()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }

        String password = ninTeacher.getTeacherPassword();
        loginService.passwordVerify(password);
        ninTeacher.setTeacherPassword(MD5.getMD5Encode(password));

        return ninTeacherMapper.updateById(ninTeacher);
    }

    @Override
    public NinTeacher getTeacherById(Long id) {
        return getById(id);
    }


    @Override
    public List<NinTeacher> getTeaAll() {
        UserInfo userInfo = UserUtil.getUserInfo();
        LambdaQueryWrapper<NinTeacher> wrapper = new LambdaQueryWrapper<NinTeacher>()
                .select(NinTeacher::getId, NinTeacher::getTeacherName)
                .eq(userInfo.getUserType().equals(UserTypeEnum.TEACHER.getName()),
                        NinTeacher::getId, userInfo.getUserId());
        return ninTeacherMapper.selectList(wrapper);

    }

}
