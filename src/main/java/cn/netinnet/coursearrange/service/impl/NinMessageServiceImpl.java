package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinMessage;
import cn.netinnet.coursearrange.mapper.NinMessageMapper;
import cn.netinnet.coursearrange.service.INinMessageService;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import java.util.List;


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

    @Override
    public Page<NinMessage> getMsgList(Integer page, Integer size) {
        Page<NinMessage> msgPage = new Page<>(page, size);
        LambdaQueryWrapper<NinMessage> wrapper = new LambdaQueryWrapper<NinMessage>()
                .orderByAsc(NinMessage::getIsRead).orderByDesc(NinMessage::getCreateTime);
        Page<NinMessage> messagePage = this.page(msgPage, wrapper);
        messagePage.getRecords().forEach(msg -> {
            if (msg.getIsConsent() != -1) {
                //对msg进行操作
            }
        });
        return messagePage;
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

}
