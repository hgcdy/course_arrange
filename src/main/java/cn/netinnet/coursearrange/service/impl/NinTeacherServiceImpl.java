package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinTeacherCourseMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherMapper;
import cn.netinnet.coursearrange.service.INinTeacherService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.MD5;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, String teacherName) {
        UserInfo userInfo = UserUtil.getUserInfo();
        HashMap<String, Object> map = new HashMap<>();
        if (userInfo.getUserType().equals("teacher")) {
            NinTeacher ninTeacher = ninTeacherMapper.selectById(userInfo.getUserId());
            List<NinTeacher> list = new ArrayList<>();
            list.add(ninTeacher);
            map.put("list", list);
            map.put("total", 1);
        } else {
            PageHelper.startPage(page, size);
            List<Map<String, Object>> list = ninTeacherMapper.getSelectList(teacherName).stream().map(i -> {
                i.put("id", i.get("id").toString());
                return i;
            }).collect(Collectors.toList());
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
            map.put("list", pageInfo.getList());
            map.put("total", pageInfo.getTotal());
        }
        return map;
    }

    @Override
    public int addSingle(NinTeacher ninTeacher) {
        Integer integer = ninTeacherMapper.selectCount(
                new QueryWrapper<NinTeacher>()
                        .eq("teacher_code", ninTeacher.getTeacherCode()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }

        String password = ninTeacher.getTeacherPassword();
        if (password != null) {
            if (password.length() < 6) {
                throw new ServiceException(412, "密码需大于六位数");
            }
            if (StringUtils.isBlank(password)) {
                throw new ServiceException(412, "密码不符合条件");
            }
            ninTeacher.setTeacherPassword(MD5.getMD5Encode(ninTeacher.getTeacherPassword()));
        }

        ninTeacher.setId(IDUtil.getID());
        ninTeacher.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninTeacher.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninTeacherMapper.insert(ninTeacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        int i = ninTeacherMapper.deleteById(id);
        //删除教师-课程表
        ninTeacherCourseMapper.delete(new QueryWrapper<>(new NinTeacherCourse(){{
            setTeacherId(id);
        }}));
        return i;
    }

    @Override
    public int alterSingle(NinTeacher ninTeacher) {
        //名字，编号重名验证
        Integer integer = ninTeacherMapper.selectCount(
                new QueryWrapper<NinTeacher>()
                        .ne("id", ninTeacher.getId())
                        .eq("teacher_code", ninTeacher.getTeacherCode()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }

        String password = ninTeacher.getTeacherPassword();
        if (password != null) {
            if (password.length() < 6) {
                throw new ServiceException(412, "密码需大于六位数");
            }
            if (StringUtils.isBlank(password)) {
                throw new ServiceException(412, "密码不符合条件");
            }
            ninTeacher.setTeacherPassword(MD5.getMD5Encode(ninTeacher.getTeacherPassword()));
        }

        ninTeacher.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninTeacherMapper.updateById(ninTeacher);
    }

    @Override
    public NinTeacher getTeacherById(Long id) {
        return ninTeacherMapper.selectById(id);
    }


    @Override
    public NinTeacher verify(String code, String password) {
        NinTeacher ninTeacher = ninTeacherMapper.selectOne(new QueryWrapper<>(new NinTeacher() {{
            setTeacherCode(code);
        }}));
        if (ninTeacher != null) {
            if (ninTeacher.getTeacherPassword().equals(MD5.getMD5Encode(password))) {
                return ninTeacher;
            } else {
                throw new ServiceException(412, "密码错误");
            }
        } else {
            throw new ServiceException(412, "账号不存在");
        }
    }
}
