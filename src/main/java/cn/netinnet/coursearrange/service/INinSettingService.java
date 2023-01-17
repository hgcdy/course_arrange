package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.SettingBo;
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
     */
    List<SettingBo> getSelectList(String userType, Integer openState, String courseName);
    /**
     * 修改设置
     */
    ResultModel alterBatch(String settingIds, String userType, String openTime, String closeTime);
    /**
     * 添加或更新quartz定时器任务
     */
    void addTimer(List<NinSetting> ninSettingList);
    /**
     * 根据课程id删除setting记录和quartz任务
     */
    void delTimerByCourseId(Long courseId);
}
