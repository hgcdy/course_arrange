package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinMessage;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.enums.MsgEnum;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.mapper.NinMessageMapper;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinMessageService;
import cn.netinnet.coursearrange.util.CnUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2023-01-14
 */
@Service
public class NinMessageServiceImpl extends ServiceImpl<NinMessageMapper, NinMessage> implements INinMessageService {

    @Autowired
    private INinArrangeService ninArrangeService;
    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinTeacherMapper ninTeacherMapper;

    @Override
    public PageInfo<NinMessage> getMsgList(Integer page, Integer size) {
        LambdaQueryWrapper<NinMessage> wrapper = new LambdaQueryWrapper<NinMessage>()
                .eq(NinMessage::getUserId, UserUtil.getUserInfo().getUserId())
                .orderByAsc(NinMessage::getIsRead).orderByDesc(NinMessage::getCreateTime);
        PageHelper.startPage(page, size);
        List<NinMessage> list = list(wrapper);
        PageInfo<NinMessage> pageInfo = new PageInfo<>(list);

        pageInfo.getList().forEach(msg -> {
            if (msg.getIsConsent() != -1) {
                //对msg进行操作
                JSONObject jsonObject = (JSONObject) JSONObject.parse(msg.getMsg());
                Integer weekly = jsonObject.getInteger("weekly");
                Integer week = jsonObject.getInteger("week");
                Integer pitchNum = jsonObject.getInteger("pitchNum");
                String message = String.format(MsgEnum.HOUSE_APPLY.getMsg(),
                        jsonObject.get("teacherName"), jsonObject.get("houseName"),
                        jsonObject.get("className"), CnUtil.cnNum(weekly), CnUtil.cnWeek(week),
                        CnUtil.cnNum(pitchNum), jsonObject.get("courseName"));
                msg.setMsg(message);
            }
        });
        return pageInfo;
    }

    @Override
    public boolean delMsg(Long id) {
        LambdaQueryWrapper<NinMessage> wrapper = new LambdaQueryWrapper<NinMessage>().eq(NinMessage::getIsRead, 1);
        if (null == id) {
            Long userId = UserUtil.getUserInfo().getUserId();
            wrapper.eq(NinMessage::getUserId, userId);
        } else {
            wrapper.eq(NinMessage::getId, id);
        }
        return remove(wrapper);
    }

    @Override
    public boolean readMag(Long id) {
        LambdaUpdateWrapper<NinMessage> wrapper = new LambdaUpdateWrapper<NinMessage>().set(NinMessage::getIsRead, 1);
        if (null == id) {
            Long userId = UserUtil.getUserInfo().getUserId();
            wrapper.eq(NinMessage::getUserId, userId).eq(NinMessage::getIsConsent, -1);
        } else {
            wrapper.eq(NinMessage::getId, id);
        }
        return update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consentMsg(Long id, Integer isConsent) {
        NinMessage message = getById(id);
        String msg = message.getMsg();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(msg);

        if (isConsent == 1) {
            ninArrangeService.addArrange(jsonObject);
        }

        NinMessage ninMessage = new NinMessage();
        ninMessage.setUserId(jsonObject.getLong("teacherId")); //教师id
        ninMessage.setMsg(MsgEnum.codeOfKey(isConsent).getMsg());
        save(ninMessage);

        //修改状态
        message.setIsConsent(isConsent);
        message.setIsRead(1);
        updateById(message);
    }

    @Override
    public void addBatchMsg(List<Long> userIdList, String userType, MsgEnum msgEnum, Object... objects) {
        String msg = String.format(msgEnum.getMsg(), objects);
        addBatchMsg(userIdList, userType, msg);
    }

    @Override
    public void addBatchMsg(List<Long> userIdList, String userType, String msg) {
        if (null == userIdList || userIdList.isEmpty()) {
            if (null != userType) {
                if (userType.equals(UserTypeEnum.STUDENT.getName())) {
                    userIdList = ninStudentMapper.selectList(new LambdaQueryWrapper<NinStudent>()
                                    .select(NinStudent::getId))
                            .stream().map(NinStudent::getId).collect(Collectors.toList());
                } else if (userType.equals(UserTypeEnum.TEACHER.getName())) {
                    userIdList = ninTeacherMapper.selectList(new LambdaQueryWrapper<NinTeacher>()
                                    .select(NinTeacher::getId))
                            .stream().map(NinTeacher::getId).collect(Collectors.toList());
                }
            } else {
                return;
            }
        }

        List<NinMessage> msgList = new ArrayList<>();
        userIdList.forEach(id -> {
            NinMessage ninMessage = new NinMessage();
            ninMessage.setMsg(msg);
            ninMessage.setUserId(id);
            msgList.add(ninMessage);
        });
        saveBatch(msgList);
    }
}
