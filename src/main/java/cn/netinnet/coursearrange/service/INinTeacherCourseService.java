package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
public interface INinTeacherCourseService extends IService<NinTeacherCourse> {
    /**
     * 查询
     */
    List<ContactCourseBo> getSelectList(Long teacherId);
    /**
     * 单个新增
     */
    int addSingle(NinTeacherCourse ninTeacherCourse);
    /**
     * 单个删除
     */
    int delById(Long id);
}
