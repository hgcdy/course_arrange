package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.StudentBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinStudentService;
import cn.netinnet.coursearrange.service.LoginService;
import cn.netinnet.coursearrange.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Service
public class NinStudentServiceImpl extends ServiceImpl<NinStudentMapper, NinStudent> implements INinStudentService {

    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinCareerMapper ninCareerMapper;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinMessageMapper ninMessageMapper;
    @Autowired
    private LoginService loginService;


    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, String college, Long careerId, Long classId, String studentName) {
        List<Long> careerIds = null;
        if (careerId == null) {
            //若专业id为空，查看学院
            if (college != null && !StringUtils.isBlank(college)) {
                //学院不为空，学院->专业id列表
                List<NinCareer> ninCareers = ninCareerMapper.selectList(new QueryWrapper<>(new NinCareer() {{
                    setCollege(college);
                }}));
                careerIds = ninCareers.stream().map(NinCareer::getId).collect(Collectors.toList());
            }
        } else {
            //专业id不为空，将专业id放进去
            careerIds = new ArrayList<>(1);
            careerIds.add(careerId);
        }
        PageHelper.startPage(page, size);
        List<StudentBo> list = ninStudentMapper.getSelectList(careerIds, classId, studentName);
        PageInfo<StudentBo> pageInfo = new PageInfo<>(list);
        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }


    @Override
    public int addSingle(NinStudent ninStudent) {
        //查询班级是否存在
        NinClass ninClass = ninClassMapper.selectById(ninStudent.getClassId());
        if (ninClass == null) {
            throw new ServiceException(412, "班级不存在，请先创建班级");
        }

        //限制班级人数
        if (ninClass.getPeopleNum() >= ApplicationConstant.CLASS_PEOPLE_NUM) {
            throw new ServiceException(412, "该班级人数过多");
        }

        //班级人数+1
        ninClassMapper.addPeopleNum(ninStudent.getClassId());
        int count = count(new LambdaQueryWrapper<NinStudent>()
                .eq(NinStudent::getStudentCode, ninStudent.getStudentCode()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }

        String password = ninStudent.getStudentPassword();
        if (password != null) {
            if (password.length() < 6) {
                throw new ServiceException(412, "密码需大于六位数");
            }
            if (StringUtils.isBlank(password)) {
                throw new ServiceException(412, "密码不符合条件");
            }
            ninStudent.setStudentPassword(MD5.getMD5Encode(ninStudent.getStudentPassword()));
        }

        return ninStudentMapper.insert(ninStudent);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delById(Long id) {
        NinStudent ninStudent = getById(id);
        //删除学生-课程表记录
        ninStudentCourseMapper.delete(new LambdaQueryWrapper<NinStudentCourse>().eq(NinStudentCourse::getStudentId, id));
        //班级人数-1
        ninClassMapper.subPeopleNum(ninStudent.getClassId());
        //删除学生消息
        ninMessageMapper.delete(new LambdaQueryWrapper<NinMessage>().eq(NinMessage::getUserId, id));
        //删除学生
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int alterSingle(NinStudent ninStudent) {
        int count = count(new LambdaQueryWrapper<NinStudent>()
                .eq(NinStudent::getStudentName, ninStudent.getStudentName())
                .ne(NinStudent::getId, ninStudent.getId()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }
        NinStudent ninStudent1 = getById(ninStudent.getId());
        //学生修改班级
        if (!Objects.equals(ninStudent1.getClassId(), ninStudent.getClassId())) {
            ninClassMapper.subPeopleNum(ninStudent1.getClassId());
            ninClassMapper.addPeopleNum(ninStudent.getClassId());
        }

        String password = ninStudent.getStudentPassword();
        loginService.passwordVerify(password);

        return ninStudentMapper.updateById(ninStudent);
    }


    @Override
    public NinStudent getStudentById(Long id) {
        return getById(id);
    }


}
