package cn.netinnet.coursearrange.Task;

import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.service.INinSettingService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class SettingTask extends QuartzJobBean{

    @Autowired
    private INinSettingService ninSettingService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        NinSetting ninSetting = (NinSetting) dataMap.get("data1");
        LocalDateTime closeTime = ninSetting.getCloseTime();
        LocalDateTime nowTime = LocalDateTime.now();
        if (nowTime.isAfter(closeTime)) {
            ninSetting.setOpenState(2);
        } else {
            ninSetting.setOpenState(1);
            ninSettingService.addTimer(Collections.singletonList(ninSetting));
        }
        ninSettingService.updateById(ninSetting);
    }
}
