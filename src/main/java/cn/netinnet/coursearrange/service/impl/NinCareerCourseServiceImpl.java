package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.ClassBo;
import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinCareerCourse;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinCareerCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.service.INinCareerCourseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * @since 2022-09-08
 */
@Service
public class NinCareerCourseServiceImpl extends ServiceImpl<NinCareerCourseMapper, NinCareerCourse> implements INinCareerCourseService {

    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;
    @Autowired
    private NinClassMapper ninClassMapper;

    @Override
    public List<ContactCourseBo> getSelectList(Long careerId) {
        List<ContactCourseBo> list = ninCareerCourseMapper.getSelectList(careerId);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBatchCourse(List<Long> careerIdList, List<Long> courseIdList) {
        if (careerIdList == null || careerIdList.size() == 0) {
            throw new ServiceException(ResultEnum.formatMsg(ResultEnum.SELECT, "专业"));
        }
        if (courseIdList == null ||courseIdList.size() == 0) {
            throw new ServiceException(ResultEnum.formatMsg(ResultEnum.SELECT, "课程"));
        }

        //重复验证
        List<NinCareerCourse> ninCareerCourses = ninCareerCourseMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinCareerCourse>> collect = new HashMap<>();
        if (ninCareerCourses != null) {
            collect = ninCareerCourses.stream().collect(Collectors.groupingBy(NinCareerCourse::getCareerId));
        }

        ArrayList<NinCareerCourse> ninCareerCourseArrayList = new ArrayList<>();
        for (Long careerId : careerIdList) {
            List<NinCareerCourse> careerCourses = collect.get(careerId);
            //同专业的情况下
            ok: for (Long courseId : courseIdList) {
                //如果该专业下，数据库中已经存在该课程,b标记为false,跳出
                if (careerCourses != null) {
                    for (NinCareerCourse ncc : careerCourses) {
                        if (courseId.equals(ncc.getCourseId())) {
                            continue ok;
                        }
                    }
                }
                NinCareerCourse ninCareerCourse = new NinCareerCourse();
                ninCareerCourse.setCareerId(careerId);
                ninCareerCourse.setCourseId(courseId);
                ninCareerCourse.setId(IDUtil.getID());
                ninCareerCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
                ninCareerCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
                ninCareerCourseArrayList.add(ninCareerCourse);
            }
        }
        if (ninCareerCourseArrayList != null && ninCareerCourseArrayList.size() != 0) {
            //插入新的专业选课记录
            ninCareerCourseMapper.addBatchCourse(ninCareerCourseArrayList);

            //查询专业的课程数量
            List<ClassBo> bos = ninCareerCourseMapper.getCourseNum().stream().filter(i -> i.getCourseNum() > ApplicationConstant.CAREER_COURSE_NUM).collect(Collectors.toList());
            if (bos != null && bos.size() > 0) {
                throw new ServiceException(412, bos.get(0).getCareerName() + "专业的课程数量将超出上限");
            }

            //班级课程数量
            Map<Long, List<NinCareerCourse>> collect1 = ninCareerCourseArrayList.stream().collect(Collectors.groupingBy(NinCareerCourse::getCareerId));
            ArrayList<Map<String, Object>> maps = new ArrayList<>();
            for (Map.Entry<Long, List<NinCareerCourse>> map: collect1.entrySet()) {
                Map<String, Object> m = new HashMap<>();
                m.put("careerId", map.getKey());
                m.put("courseNum", map.getValue().size());
                maps.add(m);
            }
            ninClassMapper.alterBatchCourseNum(maps);

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delCareerCourse(Long id) {
        NinCareerCourse ninCareerCourse = ninCareerCourseMapper.selectById(id);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("careerId", ninCareerCourse.getCareerId());
        hashMap.put("courseNum", -1);
        maps.add(hashMap);
        ninClassMapper.alterBatchCourseNum(maps);
        return ninCareerCourseMapper.deleteById(id);
    }


}
