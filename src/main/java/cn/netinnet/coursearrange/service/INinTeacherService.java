package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
public interface INinTeacherService extends IService<NinTeacher> {

    /**
     * 分页条件查询（当是教师用户查询时，返回自身）
     * @param page
     * @param size
     * @param teacherName
     * @return
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, String teacherName);

    /**
     * 单个新增
     * @param ninTeacher
     * @return
     */
    int addSingle(NinTeacher ninTeacher);


    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);

    /**
     * 单个修改
     * @param ninTeacher
     * @return
     */
    int alterSingle(NinTeacher ninTeacher);

    NinTeacher getTeacherById(Long id);

    /**
     * 教师登录验证
     * @param code
     * @param password
     * @return
     */
    NinTeacher verify(String code, String password);
}
