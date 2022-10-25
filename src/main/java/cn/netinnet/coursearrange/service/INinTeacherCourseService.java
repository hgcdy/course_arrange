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

    /**
     * 查询
     * @param teacherId
     * @return
     */
    List<Map<String, Object>> getSelectList(Long teacherId);

    /**
     * 单个新增
     * @param ninTeacherCourse
     * @return
     */
    int addSingle(NinTeacherCourse ninTeacherCourse);


    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);

}
