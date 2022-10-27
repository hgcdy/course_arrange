package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TeacherBo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String teacherName;

    private String teacherCode;

    private String courseName;

}
