package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinCareer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
public interface INinCareerService extends IService<NinCareer> {
    /**
     * 获取学院列表
     */
    List<String> getCollegeList();
    /**
     * 获取专业列表，为空则获取全部
     */
    List<NinCareer> getNinCareerList(String college);
    /**
     * 获取学院专业列表
     */
    Map<String, List<NinCareer>> getCollegeCareerList();
    /**
     * 单个新增
     */
    int addSingle(NinCareer ninCareer);
    /**
     * 单个删除
     */
    int delById(Long id);
    /**
     * 单个修改
     */
    int alterSingle(NinCareer ninCareer);
}
