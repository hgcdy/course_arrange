package cn.netinnet.coursearrange.geneticAlgorithm;

import lombok.Data;

import java.util.List;

@Data
public class Chromosome {
    /**
     * 一组任务记录形成一个解
     */

    private List<TaskRecord> taskRecordList;
    private double score;

}
