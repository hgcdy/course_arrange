package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ContactCourseBo {

    /**
     * 关联表id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 教师id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long teacherId;

    /**
     * 学生id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long studentId;

    /**
     * 专业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long careerId;

    /**
     * 课程id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;

    private String courseName;

    private Integer houseType;

    private Integer must;

    private Integer courseTime;

    private Integer startTime;

    private Integer endTime;

    private Integer weekTime;

    private Integer week;

    private String cnWeek;

    private Integer pitchNum;

    private String cnPitchNum;

}
