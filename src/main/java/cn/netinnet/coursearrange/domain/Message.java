package cn.netinnet.coursearrange.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class Message {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String content;
    private Date sendDate;

    /**
     * 0-连接成功
     * 1-学生选课时间发生变化
     * 2-教师选课时间发生变化
     */
    private int code;

    public Message() {
    }

    public Message(Long userId, String content, int code) {
        this.userId = userId;
        this.content = content;
        this.sendDate = new Date();
        this.code = code;
    }
}
