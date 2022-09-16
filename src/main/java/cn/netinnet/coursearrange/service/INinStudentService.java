package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinStudent;
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
public interface INinStudentService extends IService<NinStudent> {

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param classIds 班级id列表
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
     * 批量删除
     * @param ids 学生id集合
     * @return
     */
    int delBatch(List<Long> ids);

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



    NinStudent getStudentById(Long id);

    /**
     * 学生用户登录
     * @param code
     * @param password
     * @return
     */
    NinStudent verify(String code, String password);
}
