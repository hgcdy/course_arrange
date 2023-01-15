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
}
