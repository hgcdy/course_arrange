package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinStudentCourse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
public interface INinStudentCourseService extends IService<NinStudentCourse> {

    /**
     * 获取学生课程
     * @param id
     * @return
     */
    Map<String, List<Map<String, Object>>> getCourse(Long id);
    /**
     * 添加
     */
    boolean addSingle(Long studentId, Long courseId);

    /**
     * 删除
     */
    boolean delSingle(Long studentId, Long courseId);
}
