package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.SettingBo;
import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.mapper.NinSettingMapper;
import cn.netinnet.coursearrange.service.INinSettingService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


    @Override
    public List<SettingBo> getSelectList(String userType, String state, String courseName) {
        List<SettingBo> result = ninSettingMapper.getSelectList(userType, courseName);
//                .stream().map(bo -> {
//            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String openTime = bo.getOpenTime();
//            String closeTime = bo.getCloseTime();
//            LocalDateTime dateTime = LocalDateTime.now();
//            int i = bo.getOpenState();
//            if (i == 0) {
//                bo.setState("开放中");
//            } else if (i == 1){
//                bo.setState("未开放");
//            } else if (i == 2) {
//                boolean after = dateTime.isAfter(LocalDateTime.parse(openTime, dtf));
//                boolean after1 = dateTime.isAfter(LocalDateTime.parse(closeTime, dtf));
//                if (!after) {
//                    bo.setState("未开放");
//                } else if (after && !after1){
//                    bo.setState("开放中");
//                } else {
//                    bo.setState("已结束");
//                }
//            }
//            return bo;
//        }).collect(Collectors.toList());
        if (state != null && !state.equals("")) {
            result = result.stream().filter(i -> i.getState().equals(state)).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public ResultEntry alterBatch(String settingIds, Integer openState, String openTime, String closeTime) {
        List<Long> settingIdList = JSON.parseArray(settingIds, Long.class);

        if (settingIdList != null && settingIdList.size() != 0) {
            if (openState == 2) {
                if (LocalDateTime.parse(openTime).isAfter(LocalDateTime.parse(closeTime))) {
                    throw new ServiceException(412, "开始时间大于结束时间,请重试");
                }
                ninSettingMapper.alterBatch(settingIdList, openState, LocalDateTime.parse(openTime), LocalDateTime.parse(closeTime));
            } else {
                ninSettingMapper.alterBatch(settingIdList, openState, null, null);
            }
            return ResultEntry.ok();
        } else {
            throw new ServiceException(412, "请选择课程！");
        }
    }


}
