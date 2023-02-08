package cn.netinnet.coursearrange.geneticAlgorithm;

import cn.netinnet.coursearrange.geneticAlgorithm.domain.TaskRecord;

import java.util.List;

public interface GeneticAlgorithm {
    List<TaskRecord> start();

    /**
     * 遍历全部校验并解决硬冲突
     * @param taskRecordList
     * @return 冲突个数
     */
    int verifyClashSolve(List<TaskRecord> taskRecordList);
}
