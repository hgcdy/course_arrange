package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinCourse;
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
public interface INinCourseService extends IService<NinCourse> {

    /**
     * 分页条件查询
     * @param page 页码
     * @param size 页数
     * @param ninCourse 实体类
     * @return
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, NinCourse ninCourse);

    /**
     * 单条查询
     * @param id
     * @return
     */
    NinCourse getCourseById(Long id);

    /**
     * 单个新增
     * @param ninCourse
     * @return
     */
    int addSingle(NinCourse ninCourse);


    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);

    /**
     * 单个修改
     * @param ninCourse
     * @return
     */
    int alterSingle(NinCourse ninCourse);

    /**
     * 返回可选的课程
     * @param classId
     * @return
     */
    List<NinCourse> getSelectCourseList(Integer sign);
}
