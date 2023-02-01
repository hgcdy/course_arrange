package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.task.SettingTask;
import cn.netinnet.coursearrange.bo.SettingBo;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.enums.OpenStateEnum;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinSettingMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinSettingService;
import cn.netinnet.coursearrange.util.QuartzManager;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-10-19
 */
@Service
public class NinSettingServiceImpl extends ServiceImpl<NinSettingMapper, NinSetting> implements INinSettingService {

    @Autowired
    private NinSettingMapper ninSettingMapper;
    @Autowired
    private INinArrangeService ninArrangeService;
    @Autowired
    private QuartzManager quartzManager;


    @Override
    public List<SettingBo> getSelectList(String userType, Integer openState, String courseName) {
        List<NinSetting> ninSettingList = list(new LambdaQueryWrapper<NinSetting>().eq(NinSetting::getUserType, userType)
                .eq(null != openState, NinSetting::getOpenState, openState)
                .like(null != courseName && !"".equals(courseName), NinSetting::getCourseName, courseName));

        return ninSettingList.stream().map(i -> {
            SettingBo bo = new SettingBo();
            BeanUtils.copyProperties(i, bo);
            bo.setState(OpenStateEnum.codeOfKey(bo.getOpenState()).getName());
            if (null != bo.getOpenTime()) {
                bo.setOpenTimestamp(bo.getOpenTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                bo.setCloseTimestamp(bo.getCloseTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            return bo;
        }).collect(Collectors.toList());
    }

    @Override
    public ResultModel alterBatch(String settingIds, String userType, String openTime, String closeTime) {
        if (UserTypeEnum.STUDENT.getName().equals(userType)) {
            NinArrange arrange = ninArrangeService.getOne(new LambdaQueryWrapper<NinArrange>()
                    .ne(NinArrange::getCareerId, 0)
                    .ne(NinArrange::getCareerId, -1), false);
            if (arrange == null) {
                throw new ServiceException(412, "请在排课后再开放学生选课通道");
            }
        }

        List<Long> settingIdList = JSON.parseArray(settingIds, Long.class);

        if (settingIdList != null && settingIdList.size() != 0) {
            LocalDateTime openDate = LocalDateTime.parse(openTime);
            LocalDateTime closeDate = LocalDateTime.parse(closeTime);
            if (openDate.isAfter(closeDate)) {
                throw new ServiceException(412, "开始时间大于结束时间,请重试");
            }
            LocalDateTime nowDate = LocalDateTime.now();
            int openState;
            if (openDate.isAfter(nowDate)) {
                openState = 0;
            } else if (nowDate.isAfter(closeDate)) {
                openState = 2;
            } else {
                openState = 1;
            }

            ninSettingMapper.alterBatch(settingIdList, openState, openDate, closeDate);
            List<NinSetting> ninSettings = ninSettingMapper.selectList(new LambdaQueryWrapper<NinSetting>()
                    .in(NinSetting::getId, settingIdList));
            addTimer(ninSettings);
            return ResultModel.ok();
        } else {
            throw new ServiceException(412, "请选择课程！");
        }
    }

    @Override
    public void addTimer(List<NinSetting> ninSettingList) {
        if (ninSettingList != null && !ninSettingList.isEmpty()) {
            ninSettingList.forEach(setting -> {
                Calendar calendar = Calendar.getInstance();
                LocalDateTime openTime = setting.getOpenTime();
                LocalDateTime closeTime = setting.getCloseTime();
                Integer openState = setting.getOpenState();

                if (null != openTime && null != closeTime) {
                    String idStr = setting.getId().toString();
                    String userType = setting.getUserType();
                    if (openState == 2) {
                        quartzManager.removeJob(idStr, userType, idStr, userType);
                    } else {
                        if (openState == 0) {
                            Date openDate = Date.from(openTime.atZone(ZoneId.systemDefault()).toInstant());
                            calendar.setTime(openDate);
                        } else {
                            Date closeDate = Date.from(closeTime.atZone(ZoneId.systemDefault()).toInstant());
                            calendar.setTime(closeDate);
                        }
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(calendar.get(Calendar.SECOND)).append(" ").append(calendar.get(Calendar.MINUTE)).append(" ").append(calendar.get(Calendar.HOUR_OF_DAY)).append(" ")
                                .append(calendar.get(Calendar.DATE)).append(" ").append(calendar.get(Calendar.MONTH) + 1).append(" ? ").append(calendar.get(Calendar.YEAR));
                        quartzManager.modifyJobTime(idStr, userType, idStr, userType, SettingTask.class, buffer.toString(), setting);
                    }
                }
            });
        }
    }

    @Override
    public void delTimerByCourseId(Long courseId) {
        LambdaQueryWrapper<NinSetting> wrapper = new LambdaQueryWrapper<NinSetting>().eq(NinSetting::getCourseId, courseId);
        List<NinSetting> ninSettings = list(wrapper);
        ninSettings.forEach(setting -> {
            String idStr = setting.getId().toString();
            String userType = setting.getUserType();
            quartzManager.removeJob(idStr, userType, idStr, userType);
        });
        remove(wrapper);
    }


}
