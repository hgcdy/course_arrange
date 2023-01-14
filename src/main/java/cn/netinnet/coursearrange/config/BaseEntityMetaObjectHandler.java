package cn.netinnet.coursearrange.config;

import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class BaseEntityMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        LocalDateTime now = LocalDateTime.now();
        Long userId = UserUtil.getUserInfo().getUserId();

        this.strictInsertFill(metaObject, "createUserId", Long.class, userId);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "modifyUserId",Long.class, userId);
        this.strictInsertFill(metaObject, "modifyTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        Long userId = UserUtil.getUserInfo().getUserId();
        this.strictInsertFill(metaObject, "modifyUserId",Long.class, userId);
        this.strictInsertFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now());
    }
}
