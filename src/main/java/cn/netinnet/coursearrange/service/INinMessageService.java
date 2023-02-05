package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinMessage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

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

    PageInfo<NinMessage> getMsgList(Integer page, Integer size);

    boolean delMsg(Long id);

    boolean readMag(Long id);

    void consentMsg(Long id, Integer isConsent);
}
