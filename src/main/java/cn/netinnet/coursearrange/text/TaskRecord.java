package cn.netinnet.coursearrange.text;

import lombok.Data;

@Data
public class TaskRecord {
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
