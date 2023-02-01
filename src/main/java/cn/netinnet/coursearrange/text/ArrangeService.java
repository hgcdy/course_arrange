package cn.netinnet.coursearrange.text;

import java.util.List;

public interface ArrangeService {

    //生成一个解
    List<TaskRecord> generateChromosome();

    boolean solveClash(List<TaskRecord> taskRecordList, TaskRecord taskRecord);

    boolean verifyClash(List<TaskRecord> taskRecordList, TaskRecord taskRecord);

}
