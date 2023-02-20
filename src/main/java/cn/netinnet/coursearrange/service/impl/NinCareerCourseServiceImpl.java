package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinCareerCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinCareerCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.service.INinCareerCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * @since 2022-09-08
 */
@Service
public class NinCareerCourseServiceImpl extends ServiceImpl<NinCareerCourseMapper, NinCareerCourse> implements INinCareerCourseService {

    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;
    @Autowired
    private NinClassMapper ninClassMapper;

    @Override
    public List<Map<String, Object>> getSelectList(Long careerId) {
        List<Map<String, Object>> selectList = ninCareerCourseMapper.getSelectList(careerId);
        selectList.forEach(i -> {
            i.put("id", i.get("id").toString());
        });
        return selectList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBatchCourse(List<Long> careerIdList, List<Long> courseIdList) {
        if (careerIdList == null || careerIdList.isEmpty()) {
            throw new ServiceException(412, "请选择专业");
        }
        if (courseIdList == null ||courseIdList.isEmpty()) {
            throw new ServiceException(412, "请选择课程");
        }

        //结果集
        ArrayList<NinCareerCourse> ninCareerCourseArrayList = new ArrayList<>();
        //重复验证
        List<NinCareerCourse> ninCareerCourses = ninCareerCourseMapper.selectList(new QueryWrapper<>());

        for (Long careerId : careerIdList) {
            for (Long courseId : courseIdList) {
                NinCareerCourse ninCareerCourse = new NinCareerCourse(careerId, courseId);
                if (verify(ninCareerCourse, ninCareerCourses)) {
                    ninCareerCourseArrayList.add(ninCareerCourse);
                }
            }
        }

        if (ninCareerCourseArrayList.size() != 0) {
            //插入新的专业选课记录
            saveBatch(ninCareerCourseArrayList);

            //班级课程数量
            Map<Long, List<NinCareerCourse>> courseNumMap = ninCareerCourseArrayList.stream().collect(Collectors.groupingBy(NinCareerCourse::getCareerId));
            ArrayList<Map<String, Object>> maps = new ArrayList<>();
            for (Map.Entry<Long, List<NinCareerCourse>> map: courseNumMap.entrySet()) {
                Map<String, Object> m = new HashMap<>();
                m.put("careerId", map.getKey());
                m.put("courseNum", map.getValue().size());
                maps.add(m);
            }
            ninClassMapper.alterBatchCourseNum(maps);
        }
    }

    public boolean verify(NinCareerCourse ninCareerCourse, List<NinCareerCourse> ninCareerCourseList) {
        for (NinCareerCourse careerCourse : ninCareerCourseList) {
            if (careerCourse.getCourseId().equals(ninCareerCourse.getCourseId())
                    && careerCourse.getCareerId().equals(ninCareerCourse.getCareerId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delCareerCourse(Long id) {
        NinCareerCourse ninCareerCourse = getById(id);
        Map<String, Object> map = new HashMap<String, Object>(){{
            put("careerId", ninCareerCourse.getCareerId());
            put("courseNum", -1);
        }};
        ninClassMapper.alterBatchCourseNum(Collections.singletonList(map));
        return removeById(id);
    }


}
