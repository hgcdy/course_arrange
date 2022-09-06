package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinTeacherCourseMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherMapper;
import cn.netinnet.coursearrange.service.INinTeacherService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, String teacherName) {
        PageHelper.startPage(page, size);
        List<NinTeacher> list = ninTeacherMapper.getSelectList(teacherName);
        PageInfo<NinTeacher> pageInfo = new PageInfo<>(list);
        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public int addSingle(NinTeacher ninTeacher) {
        Integer integer = ninTeacherMapper.selectCount(
                new QueryWrapper<NinTeacher>()
                        .eq("teacher_name", ninTeacher.getTeacherName())
                        .or().eq("teacher_code", ninTeacher.getTeacherCode()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
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
                        .and(i -> i.eq("teacher_name", ninTeacher.getTeacherName())
                                .or().eq("teacher_code", ninTeacher.getTeacherCode())));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }
        ninTeacher.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninTeacherMapper.updateById(ninTeacher);
    }

    @Override
    public NinTeacher getTeacherById(Long id) {
        return ninTeacherMapper.selectById(id);
    }
}
