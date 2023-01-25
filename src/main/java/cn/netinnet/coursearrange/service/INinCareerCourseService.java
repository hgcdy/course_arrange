package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinCareerCourse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
     */
    List<ContactCourseBo> getSelectList(Long careerId);
    /**
     * 批量添加课程
     */
    void addBatchCourse(List<Long> careerIdList, List<Long> courseIdList);
    /**
     * 根据id删除
     */
    boolean delCareerCourse(Long id);
}
