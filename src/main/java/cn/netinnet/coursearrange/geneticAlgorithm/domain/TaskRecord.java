package cn.netinnet.coursearrange.geneticAlgorithm.domain;

import lombok.Data;

@Data
public class TaskRecord {
    /**
     * 教学任务加上教室和时间形成一条任务记录
     */

    //教学任务
    private TeaTask teaTask;

    private Long houseId;
    private Integer seat;

    private Integer weekly;
    private Integer week;
    private Integer pitchNum;

    public TaskRecord(TeaTask teaTask) {
        this.teaTask = teaTask;
    }

    public TaskRecord() {
    }
}
