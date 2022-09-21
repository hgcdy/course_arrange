package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinCareerCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinCareerCourseMapper;
import cn.netinnet.coursearrange.service.INinCareerCourseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<Map<String, Object>> getSelectList(Long careerId) {
        List<Map<String, Object>> list = ninCareerCourseMapper.getSelectList(careerId);
        Utils.conversion(list);
        return list;
    }

    @Override
    public void addBatchCourse(List<Long> careerIdList, List<Long> courseIdList) {
        if (careerIdList == null || careerIdList.size() == 0) {
            throw new ServiceException(412, "请选择专业");
        }
        if (courseIdList == null ||courseIdList.size() == 0) {
            throw new ServiceException(412, "请选择课程");
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
            for (Long courseId : courseIdList) {
                //如果该专业下，数据库中已经存在该课程,b标记为false,跳出
                boolean b = true;//标记
                if (careerCourses != null) {
                    for (NinCareerCourse ncc : careerCourses) {
                        if (courseId == ncc.getCourseId()) {
                            b = false;
                            break;
                        }
                    }
                }
                if (b) {
                    NinCareerCourse ninCareerCourse = new NinCareerCourse();
                    ninCareerCourse.setCareerId(careerId);
                    ninCareerCourse.setCourseId(courseId);
                    ninCareerCourse.setId(IDUtil.getID());
                    ninCareerCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
                    ninCareerCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
                    ninCareerCourseArrayList.add(ninCareerCourse);
                }
            }
        }
        if (ninCareerCourseArrayList != null && ninCareerCourseArrayList.size() != 0) {
            ninCareerCourseMapper.addBatchCourse(ninCareerCourseArrayList);
        }
    }

    @Override
    public int delCareerCourse(Long id) {
        return ninCareerCourseMapper.deleteById(id);
    }


}
