package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinStudentCourse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
     * 查询
     */
    List<List<ContactCourseBo>> getSelectList(Long studentId);
    /**
     * 添加
     */
    int addSingle(NinStudentCourse ninStudentCourse);

    /**
     * 删除
     */
    int delSingle(Long id);
}
