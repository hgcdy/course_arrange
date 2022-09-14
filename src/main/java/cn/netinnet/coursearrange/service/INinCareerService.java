package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinCareer;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * 获取专业列表，为空则获取全部
     * @param college
     * @return
     */
    List<NinCareer> getNinCareerList(String college);

}
