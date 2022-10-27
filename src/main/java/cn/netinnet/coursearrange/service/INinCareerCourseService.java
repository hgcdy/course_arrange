package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.UserCourseBo;
import cn.netinnet.coursearrange.entity.NinCareerCourse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
public interface INinCareerCourseService extends IService<NinCareerCourse> {

    /**
     * 条件查询
     * @param careerId
     * @return
     */
    List<UserCourseBo> getSelectList(Long careerId);

    /**
     * 批量添加课程
     * @param careerIdList
     * @param courseIdList
     */
    void addBatchCourse(List<Long> careerIdList, List<Long> courseIdList);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    int delCareerCourse(Long id);



}
