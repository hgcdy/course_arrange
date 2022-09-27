package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinClassCourse;
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
public interface INinClassCourseService extends IService<NinClassCourse> {

    /**
     * 该班级下的信息
     * @param classId
     * @return
     */
    List<Map<String, Object>> getSelectList(Long classId);

    /**
     * 单个新增
     * @param ninClassCourse
     * @return
     */
    int addSingle(NinClassCourse ninClassCourse);


    /**
     * 批量新增
     * @param career
     * @param courseId
     * @return
     */
    int addBatch(String career, String className, Long courseId);

    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);


}
