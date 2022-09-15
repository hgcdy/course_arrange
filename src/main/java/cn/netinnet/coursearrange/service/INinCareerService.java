package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinCareer;
import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.model.ResultModel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
     * @return
     */
    List<String> getCollegeList();

    /**
     * 获取专业列表，为空则获取全部
     * @param college
     * @return
     */
    List<NinCareer> getNinCareerList(String college);

    /**
     * 单个新增
     * @param ninCareer
     * @return
     */
    int addSingle(NinCareer ninCareer);


    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);

    /**
     * 单个修改
     * @param ninCareer
     * @return
     */
    int alterSingle(NinCareer ninCareer);





}
