package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinMessage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2023-01-14
 */
public interface INinMessageService extends IService<NinMessage> {

    Page<NinMessage> getMsgList(Integer page, Integer size);

    boolean delBatchMsg(List<Long> msgIdList);

    boolean readBatchMag(List<Long> msgIdList);
}
