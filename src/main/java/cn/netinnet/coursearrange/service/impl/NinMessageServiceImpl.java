package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinMessage;
import cn.netinnet.coursearrange.mapper.NinMessageMapper;
import cn.netinnet.coursearrange.service.INinMessageService;
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
                .orderByDesc(NinMessage::getCreateTime);
        return this.page(msgPage, wrapper);
    }

    @Override
    public boolean delBatchMsg(List<Long> msgIdList) {
        return removeByIds(msgIdList);
    }

    @Override
    public boolean readBatchMag(List<Long> msgIdList) {
        return update(new LambdaUpdateWrapper<NinMessage>()
                .set(NinMessage::getIsRead, 1)
                .in(NinMessage::getId, msgIdList));
    }
}
