package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinClass;
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
     * @param page
     * @param size
     * @param careerId
     * @return
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size, String college, Long careerId, String className);

    /**
     * 专业查询
     * @return
     */
    List<String> careerList();

    /**
     * 专业班级列表
     * @return
     */
    Map<String, List<NinClass>> careerClassList();

    /**
     * 单个新增
     * @param ninClass
     * @return
     */
    int addSingle(NinClass ninClass);

    /**
     * 删除该班级的所有学生
     * @param classId
     * @return
     */
    int delBatchStudent(Long classId);

    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);

    /**
     * 单个修改
     * @param ninClass
     * @return
     */
    int alterSingle(NinClass ninClass);

}
