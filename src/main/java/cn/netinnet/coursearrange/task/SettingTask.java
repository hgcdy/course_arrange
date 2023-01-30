package cn.netinnet.coursearrange.task;

import cn.netinnet.coursearrange.config.WebSocketServer;
import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.service.INinSettingService;
import lombok.SneakyThrows;
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

    @SneakyThrows
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
        int code = ninSetting.getUserType().equals(UserTypeEnum.STUDENT.getName()) ? 1 : 2;

        WebSocketServer.sendBatchInfo("课程【" + ninSetting.getCourseName() + "】状态发生变化，请手动刷新", null, code);
    }
}
