package cn.netinnet.coursearrange.entity;

import cn.netinnet.coursearrange.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjs
 * @since 2023-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_message")
public class NinMessage extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("user_id")
    private Long userId;

    /**
     * 是否已读(0-未读， 1-已读)
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 是否同意(-1-无需操作,0-未操作,1-同意,2-拒绝)
     */
    @TableField("is_consent")
    private Integer isConsent;

    /**
     * 消息内容
     */
    private String msg;
}
