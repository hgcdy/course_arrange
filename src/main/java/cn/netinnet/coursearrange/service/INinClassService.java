package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.ClassBo;
import cn.netinnet.coursearrange.entity.NinClass;
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
public interface INinClassService extends IService<NinClass> {
    /**
     * 分页条件查询
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, String college, Long careerId, String className);
    /**
     * 班级选的课程
     */
    List<NinCourse> getCourseList(Long classId);
    /**
     * 根据条件获取班级列表
     */
    List<ClassBo> getClassList(String college, Long careerId);
    /**
     * 全部的学院专业班级列表
     */
    Map<String, Map<String, List<ClassBo>>> collegeCareerClassList();
    /**
     * 单个新增
     */
    int addSingle(NinClass ninClass);
    /**
     * 删除该班级的所有学生
     */
    int delBatchStudent(Long classId);
    /**
     * 单个删除
     */
    int delById(Long id);
    /**
     * 单个修改
     */
    int alterSingle(NinClass ninClass);
}
