package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.model.ResultModel;
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
public interface INinStudentService extends IService<NinStudent> {
    /**
     * 分页条件查询
     * @param studentName 名字或账号
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, String college, Long careerId, Long classId, String studentName);
    /**
     * 单个新增
     */
    int addSingle(NinStudent ninStudent);
    /**
     * 单个删除
     */
    boolean delById(Long id);
    /**
     * 单个修改
     */
    int alterSingle(NinStudent ninStudent);
    /**
     * 根据id查询单条记录
     */
    NinStudent getStudentById(Long id);

}
