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
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, NinCourse ninCourse);
    /**
     * 单条查询
     */
    NinCourse getCourseById(Long id);
    /**
     * 单个新增
     */
    int addSingle(NinCourse ninCourse);
    /**
     * 单个删除
     */
    int delById(Long id);
    /**
     * 单个修改
     */
    int alterSingle(NinCourse ninCourse);
    /**
     * 返回可选的课程
     * @param sign 0-选修，1-必修，null或其他全选
     */
    List<NinCourse> getSelectCourseList(Integer sign);

    /**
     * 获取全部课程
     * @return
     */
    List<NinCourse> getCourseAll();
}
