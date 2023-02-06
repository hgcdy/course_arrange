package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinTeacher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TeacherBo extends NinTeacher {

    private String courseName;

}
