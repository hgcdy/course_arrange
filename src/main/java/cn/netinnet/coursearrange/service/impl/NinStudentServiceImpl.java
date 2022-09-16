package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinCareer;
import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinStudentCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinCareerMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinStudentCourseMapper;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.service.INinStudentService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
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
 * 服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Service
public class NinStudentServiceImpl extends ServiceImpl<NinStudentMapper, NinStudent> implements INinStudentService {

    /**
     * 班级最大人数
     */
    private static final int MAX_PEOPLE_NUM = 50;

    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinCareerMapper ninCareerMapper;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;


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
        List<Map<String, Object>> list = ninStudentMapper.getSelectList(careerIds, classId, studentName);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Utils.conversion(pageInfo.getList());
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
        if (ninClass.getPeopleNum() >= MAX_PEOPLE_NUM) {
            throw new ServiceException(412, "该班级人数过多");
        }

        //班级人数+1
        ninClassMapper.addPeopleNum(ninStudent.getClassId());
//        Integer i = ninStudentMapper.selectCount(
//                new QueryWrapper<NinStudent>()
//                        .eq("student_name", ninStudent.getStudentName())
//                        .or().eq("student_code", ninStudent.getStudentCode()));
//        if (i > 0) {
//            throw new ServiceException(412, "重名");
//        }
        //主键id，创建者id和修改者id
        ninStudent.setId(IDUtil.getID());
        ninStudent.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninStudent.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninStudentMapper.insert(ninStudent);
    }

    @Override
    public int delBatch(List<Long> ids) {
        int i = ninStudentMapper.deleteBatchIds(ids);
        //todo 班级人数-1，但接口未使用
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        int i = ninStudentMapper.deleteById(id);
        //删除学生-课程表记录
        ninStudentCourseMapper.delete(new QueryWrapper<>(new NinStudentCourse() {{
            setStudentId(id);
        }}));
        //班级人数-1
        ninClassMapper.subPeopleNum(id);
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int alterSingle(NinStudent ninStudent) {
//        Integer i1 = ninStudentMapper.selectCount(
//                new QueryWrapper<NinStudent>()
//                        .ne("id", ninStudent.getId())
//                        .and(i -> i.eq("student_name", ninStudent.getStudentName())
//                                .or().eq("student_code", ninStudent.getStudentCode())));
//        if (i1 > 0) {
//            throw new ServiceException(412, "重名");
//        }
        NinStudent ninStudent1 = ninStudentMapper.selectById(ninStudent.getId());
        if (ninStudent1.getClassId() != ninStudent.getClassId()) {
            ninClassMapper.subPeopleNum(ninStudent1.getClassId());
            ninClassMapper.addPeopleNum(ninStudent.getClassId());
        }

        ninStudent.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninStudentMapper.updateById(ninStudent);
    }


    @Override
    public NinStudent getStudentById(Long id) {
        return ninStudentMapper.selectById(id);
    }

    @Override
    public NinStudent verify(String code, String password) {
        List<NinStudent> ninStudents = ninStudentMapper.selectList(new QueryWrapper<>(new NinStudent() {{
            setStudentCode(code);
        }}));
        if (ninStudents != null && ninStudents.size() != 0) {
            if (ninStudents.get(0).getStudentPassword().equals(password)) {
                return ninStudents.get(0);
            } else {
                throw new ServiceException(412, "密码错误");
            }
        } else {
            throw new ServiceException(412, "账号不存在");
        }
    }


}
