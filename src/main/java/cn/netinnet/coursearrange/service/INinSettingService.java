package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.NinSettingBo;
import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.model.ResultModel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-10-19
 */
public interface INinSettingService extends IService<NinSetting> {

    /**
     * 条件查询
     * @param userType
     * @param state
     * @param courseName
     * @return
     */
    List<NinSettingBo> getSelectList(String userType, String state, String courseName);

    /**
     * 修改设置
     * @param settingIds
     * @param openState
     * @param openTime
     * @param closeTime
     * @return
     */
    ResultModel alterBatch(String settingIds, Integer openState, String openTime, String closeTime);



}
