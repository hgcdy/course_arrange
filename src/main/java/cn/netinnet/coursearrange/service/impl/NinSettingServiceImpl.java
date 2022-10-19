package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinSettingMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinSettingService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    public List<Map<String, Object>> getSelectList(String userType, String state, String courseName) {
        List<Map<String, Object>> result = ninSettingMapper.getSelectList(userType, courseName).stream().map(map -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime openTime = (LocalDateTime) map.get("openTime");
            map.put("openTime", dtf.format(openTime));
            LocalDateTime closeTime = (LocalDateTime) map.get("closeTime");
            map.put("closeTime", dtf.format(closeTime));
            LocalDateTime dateTime = LocalDateTime.now();
            int i = (int) map.get("openState");
            if (i == 0) {
                map.put("state", "开放中");
            } else if (i == 1){
                map.put("state", "未开放");
            } else if (i == 2) {
                boolean after = dateTime.isAfter(openTime);
                boolean after1 = dateTime.isAfter(closeTime);
                if (!after) {
                    map.put("state", "未开放");
                } else if (after && !after1){
                    map.put("state", "开放中");
                } else {
                    map.put("state", "已结束");
                }
            }
            return map;
        }).collect(Collectors.toList());
        if (state != null && !state.equals("")) {
            result = result.stream().filter(i -> i.get("state").equals(state)).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public ResultModel alterBatch(String settingIds, Integer openState, LocalDateTime openTime, LocalDateTime closeTime) {
        List<Long> settingIdList = JSON.parseArray(settingIds, Long.class);
        if (settingIdList != null && settingIdList.size() != 0) {
            ninSettingMapper.alterBatch(settingIdList, openState, openTime, closeTime);
            return ResultModel.ok();
        } else {
            throw new ServiceException(412, "id不得为空");
        }
    }


}
