package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.model.ResultModel;
import com.baomidou.mybatisplus.extension.service.IService;
import sun.security.util.Password;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
public interface INinStudentService extends IService<NinStudent> {

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param studentName 名字或账号
     * @return
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, String college, Long careerId, Long classId, String studentName);

    /**
     * 单个新增
     * @param ninStudent
     * @return
     */
    int addSingle(NinStudent ninStudent);



    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);

    /**
     * 单个修改
     * @param ninStudent
     * @return
     */
    int alterSingle(NinStudent ninStudent);

    /**
     * 根据id查询单条记录
     * @param id
     * @return
     */
    NinStudent getStudentById(Long id);

    /**
     * 学生用户登录
     * @param code
     * @param password
     * @return
     */
    NinStudent verify(String code, String password);

    /**
     * 学生修改密码
     * @param code
     * @param oldPassword
     * @param newPassword
     * @return
     */
    ResultModel alterPassword(String code, String oldPassword, String newPassword);
}
