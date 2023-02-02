package cn.netinnet.coursearrange.geneticAlgorithm;

import java.util.List;

public interface ArrangeService {

    //生成一个解
    List<TaskRecord> generateChromosome();

    //冲突解决
    boolean solveClash(List<TaskRecord> taskRecordList, TaskRecord taskRecord);

    //冲突校验
    boolean verifyClash(List<TaskRecord> taskRecordList, TaskRecord taskRecord);

    //最终冲突校验
    int verifyClashAll(List<TaskRecord> taskRecordList);

}
