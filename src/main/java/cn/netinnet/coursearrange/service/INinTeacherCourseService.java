package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
public interface INinTeacherCourseService extends IService<NinTeacherCourse> {

    Map<String, List<Map<String, Object>>> getCourse(Long id);
    /**
     * 单个新增
     */
    boolean addSingle(Long teacherId, Long courseId);
    /**
     * 单个删除
     */
    boolean delSingle(Long teacherId, Long courseId);
}
