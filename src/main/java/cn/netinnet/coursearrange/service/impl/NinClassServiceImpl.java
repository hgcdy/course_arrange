package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinClassCourse;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinStudentCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinClassCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinStudentCourseMapper;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.service.INinClassService;
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
public class NinClassServiceImpl extends ServiceImpl<NinClassMapper, NinClass> implements INinClassService {

    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, String career, String className) {
        PageHelper.startPage(page, size);
        List<NinClass> list = ninClassMapper.getSelectList(career, className);
        PageInfo<NinClass> pageInfo = new PageInfo<>(list);
        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public List<String> careerList() {
        return ninClassMapper.careerList();
    }

    @Override
    public Map<String, List<NinClass>> careerClassList() {
        List<NinClass> classList = ninClassMapper.getSelectList(null, null);
        Map<String, List<NinClass>> map = classList.stream().collect(Collectors.groupingBy(NinClass::getCareer));
//        if (map.get("#") != null){
//            map.remove("#");
//        }
        return map;
    }

    @Override
    public int addSingle(NinClass ninClass) {
        //同名验证
        Integer integer = ninClassMapper.selectCount(
                new QueryWrapper<NinClass>()
                        .eq("class_name", ninClass.getClassName()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }
        ninClass.setId(IDUtil.getID());
        ninClass.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninClass.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninClassMapper.insert(ninClass);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delBatchStudent(Long classId) {
        NinClass ninClass = ninClassMapper.selectById(classId);
        if (ninClass.getCareer().equals("0")) {
            //选修找学生-课程表，对应的记录删除
            return ninStudentCourseMapper.delete(
                    new QueryWrapper<>(new NinStudentCourse() {{
                        setTakeClassId(classId);
                    }}));
        } else {
            //必修,删除学生->删除学生对应的其他学生-课程表记录
            List<NinStudent> ninStudents = ninStudentMapper.selectList(
                    new QueryWrapper<>(new NinStudent() {{
                        setClassId(classId);
                    }}));
            List<Long> studentIds = ninStudents.stream().map(i -> i.getId()).collect(Collectors.toList());

            if (studentIds != null && studentIds.size() != 0) {
                //根据学生ids删除学生-课程表记录
                ninStudentCourseMapper.delBatchStudentId(studentIds);
            }

            //删除学生表
            return ninStudentMapper.delete(new QueryWrapper<>(new NinStudent() {{
                setClassId(classId);
            }}));
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        //查询该班级是否还存在学生
        NinClass ninClass = ninClassMapper.selectById(id);
        if (ninClass.getPeopleNum() != 0) {
            throw new ServiceException(412, "该班级还有学生存在，删除失败");
        }
        int i = ninClassMapper.deleteById(id);
        // 删除班级-课程表的记录
        ninClassCourseMapper.delete(new QueryWrapper<>(new NinClassCourse() {{
            setClassId(id);
        }}));
        return i;
    }

    @Override
    public int alterSingle(NinClass ninClass) {
        //同名验证
        Integer integer = ninClassMapper.selectCount(
                new QueryWrapper<NinClass>()
                        .eq("class_name", ninClass.getClassName())
                        .ne("id", ninClass.getId()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }
        ninClass.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninClassMapper.updateById(ninClass);
    }


}
