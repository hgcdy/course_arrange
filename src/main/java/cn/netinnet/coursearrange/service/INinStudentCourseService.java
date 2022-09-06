package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinStudent;
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
     *
     * @param page
     * @param size
     * @param studentId
     * @return
     */
    List<List<Map<String, Object>>> getSelectList(Long studentId);

    /**
     *
     * @param ninStudentCourse
     * @return
     */
    int addSingle(NinStudentCourse ninStudentCourse);

    /**
     *
     * @param id
     * @return
     */
    int delSingle(Long id);

}
