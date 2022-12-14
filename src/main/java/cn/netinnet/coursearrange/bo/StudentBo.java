package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentBo {

    /**
     * 学生id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 学生名称
     */
    private String studentName;

    /**
     * 学生账号
     */
    private String studentCode;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long careerId;

    /**
     * 专业名称
     */
    private String careerName;

    /**
     * 班级id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;

    /**
     * 班级名称
     */
    private String className;
}
