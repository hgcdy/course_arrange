package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.global.ResultEntry;
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
public interface INinTeacherService extends IService<NinTeacher> {
    /**
     * 分页条件查询（当是教师用户查询时，返回自身）
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, String teacherName);
    /**
     * 单个新增
     */
    int addSingle(NinTeacher ninTeacher);
    /**
     * 单个删除
     */
    int delById(Long id);
    /**
     * 单个修改
     */
    int alterSingle(NinTeacher ninTeacher);
    /**
     * 根据id获取单条记录
     */
    NinTeacher getTeacherById(Long id);
    /**
     * 教师登录验证
     */
    NinTeacher verify(String code, String password);
    /**
     * 获取所有教师
     */
    List<NinTeacher> getTeaAll();
    /**
     * 学生修改密码
     */
    ResultEntry alterPassword(String code, String oldPassword, String newPassword);
}
