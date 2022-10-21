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

    List<NinSettingBo> getSelectList(String userType, String state, String courseName);

    ResultModel alterBatch(String settingIds, Integer openState, String openTime, String closeTime);



}
