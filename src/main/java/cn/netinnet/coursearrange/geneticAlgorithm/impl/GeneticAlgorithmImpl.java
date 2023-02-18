package cn.netinnet.coursearrange.geneticAlgorithm.impl;

import cn.netinnet.coursearrange.geneticAlgorithm.*;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.Chromosome;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.TaskRecord;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.TeaTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class GeneticAlgorithmImpl implements GeneticAlgorithm {

    private List<Chromosome> population = new ArrayList<Chromosome>();
    private final int popSize = 100;//种群数量
    private final int maxIterNum = 500;//最大迭代次数
    private final double mutationRate = 0.03;//基因变异的概率

    private int generation;//当前遗传到第几代

    private double bestScore;//最好得分
    private double worstScore;//最坏得分
    private double totalScore;//总得分
    private double averageScore;//平均得分
    private int invariantCount;

    private Double historyBestScore = null;//历史最优个体得分
    private int num;//历史最优个体所处代数
    private Chromosome bestChromosome;//历史最优个体

    @Autowired
    private ArrangeService arrangeService;

    @Override
    public List<TaskRecord> start() {
        //初始化种群
        init();
        while (generation < maxIterNum) {
            generation++;
            //种群遗传
            evolve();
            if (invariantCount == 20) {
                break;
            }
        }
        return bestChromosome.getTaskRecordList();
    }


    /**
     * 输出
     */
    private void print() {
        System.out.println("--------------------------------");
        System.out.println("历史最高得分:" + historyBestScore);
        System.out.println("历史最高得分代数:" + num);
        System.out.println("当前代数:" + generation);
        System.out.println("最好得分:" + bestScore);
        System.out.println("最差得分:" + worstScore);
        System.out.println("平均得分:" + averageScore);
        System.out.println("总得分:" + totalScore);
    }

    /**
     * 初始化
     */
    private void init() {
        int count = 0;
        population = new ArrayList<Chromosome>();
        while (count < popSize) {
            Chromosome chromosome = arrangeService.generateChromosome();
            if (null == chromosome) {
                continue;
            }
            setChromosomeScore(chromosome);
            count++;
            population.add(chromosome);
        }
        caculteScore();
        print();
    }

    /**
     * 遗传
     */
    private void evolve() {
        List<Chromosome> childPopulation = new ArrayList<Chromosome>();
        //生成下一代种群
        while (childPopulation.size() < popSize) {
            Chromosome p1 = getParentChromosome();
            Chromosome p2 = getParentChromosome();
            List<Chromosome> children = genetic(p1, p2);
            if (children != null) {
                childPopulation.addAll(children);
            }
        }
        //新种群替换旧种群
        List<Chromosome> t = population;
        population = childPopulation;
        t.clear();
        t = null;
        //基因突变
        mutation();
        //计算新种群的适应度
        caculteScore();
        print();
    }

    /**
     * 轮盘赌选择
     */
    private Chromosome getParentChromosome (){
        double slice = Math.random() * totalScore;
        double sum = 0;
        for (Chromosome chro : population) {
            sum += chro.getScore();
            if (sum > slice && chro.getScore() >= averageScore) {
                return chro;
            }
        }
        return null;
    }

    /**
     * 计算种群适应度
     */
    private void caculteScore() {

        Chromosome chromosome = population.get(0);
        bestScore = chromosome.getScore();
        worstScore = chromosome.getScore();
        if (null == historyBestScore || bestScore > historyBestScore) {
            historyBestScore = chromosome.getScore();
            num = generation;
            bestChromosome = chromosome;
        }
        totalScore = 0;
        for (Chromosome chro : population) {
            if (chro.getScore() > bestScore) { //设置最好基因值
                bestScore = chro.getScore();
                if (bestScore > historyBestScore) {
                    historyBestScore = chro.getScore();
                    num = generation;
                    bestChromosome = chro;
                    invariantCount = 0;
                }
            }
            if (chro.getScore() < worstScore) { //设置最坏基因值
                worstScore = chro.getScore();
            }
            totalScore += chro.getScore();
        }
        averageScore = totalScore / popSize;
        //因为精度问题导致的平均值大于最好值，将平均值设置成最好值
        averageScore = averageScore > bestScore ? bestScore : averageScore;
        invariantCount++;
    }

    /**
     * 设置染色体得分
     * @param chro
     */
    private void setChromosomeScore(Chromosome chro) {
        if (chro == null) {
            return;
        }
        /**
         * 没有冲突
         * 教师班级方差最小
         * 课程在同一天
         */


        double score = 0;
        Map<Long, int[]> idNumMap = new HashMap();

        List<TaskRecord> taskRecordList = chro.getTaskRecordList();
        int count = taskRecordList.size();//冲突个数
        //Map<周次, Map<节数, 记录列表>>
        Map<Integer, Map<Integer, List<TaskRecord>>> map = taskRecordList.stream().filter(i -> null != i.getWeek() && null != i.getPitchNum())
                .collect(Collectors.groupingBy(TaskRecord::getWeek, Collectors.groupingBy(TaskRecord::getPitchNum)));

        for (Map.Entry<Integer, Map<Integer, List<TaskRecord>>> map1: map.entrySet()) {
            Integer week = map1.getKey();
            Map<Integer, List<TaskRecord>> value = map1.getValue();

            for (Map.Entry<Integer, List<TaskRecord>> map2 : value.entrySet()) {
                Integer pitchNum = map2.getKey();
                List<TaskRecord> recordList = map2.getValue();

                count--;

                int len = recordList.size();
                for (int i = 0; i < len; i++) {
                    TaskRecord taskRecord = recordList.get(i);

                    TeaTask teaTask = taskRecord.getTeaTask();

                    List<Long> longs = new ArrayList<>();
                    longs.add(teaTask.getTeacherId());
                    longs.addAll(teaTask.getClassIdList());

                    for (Long id : longs) {
                        if (null == idNumMap.get(id)) {
                            int[] ints = new int[5];
                            ints[week - 1] = 1;
                            idNumMap.put(id, ints);
                        } else {
                            idNumMap.get(id)[week - 1] += 1;
                        }
                    }
                }
            }
        }
        score = idNumMap.size() * 2;
        for (Map.Entry<Long, int[]> map1 : idNumMap.entrySet()) {
            score -= computeVariance(map1.getValue());
        }

        //Map<教学任务, 记录列表>
        Map<TeaTask, List<TaskRecord>> taskListMap = taskRecordList.stream().collect(Collectors.groupingBy(TaskRecord::getTeaTask));
        for (Map.Entry<TeaTask, List<TaskRecord>> map1 : taskListMap.entrySet()) {
            List<TaskRecord> recordList = map1.getValue();
            int len = recordList.size();
            if (recordList.size() == 1) {
                continue;
            }
            for (int i = 0; i < len; i++) {
                for (int j = i + 1; j < len; j++) {
                    TaskRecord taskRecord1 = recordList.get(i);
                    TaskRecord taskRecord2 = recordList.get(j);

                    Integer week1 = taskRecord1.getWeek();
                    Integer week2 = taskRecord2.getWeek();

                    if (null == week1) {
                        break;
                    }
                    if (null == week2) {
                        continue;
                    }

                    //看同一个教学任务的时间
                    switch (Math.abs(week2 - week1)) {
                        case 0:
                            Integer pitchNum1 = taskRecord1.getPitchNum();
                            Integer pitchNum2 = taskRecord2.getPitchNum();
                            int sum = pitchNum1 + pitchNum2;
                            //12,23,34,45
                            if (Math.abs(pitchNum1 - pitchNum2) == 1 && (sum == 3 || sum == 7)) {
                                //同在上午或同在下午
                                score -= 5;
                            } else {
                                score -= 3;
                            }
                            break;
                        case 1:
                            //不变
                            break;
                        case 2:
                            score += 1;
                            break;
                        default:
                            score += 2;
                    }
                }
            }

        }

        //如果存在硬冲突

        int i = count / 10;
        int ii = count % 10;
        score = score * Math.pow(0.1, i) * (1 - 0.1 * ii);

        chro.setScore(score);
    }

    /**
     * 基因突变
     */
    private void mutation() {
        for (Chromosome chro : population) {
            if (Math.random() < mutationRate) { //发生基因突变
                arrangeService.mutation(chro.getTaskRecordList());
                arrangeService.verifyClashSolve(chro.getTaskRecordList());
            }
            setChromosomeScore(chro);
        }
    }

    /**
     * 克隆
     * @param c
     * @return
     */
    public Chromosome clone(final Chromosome c) {
        if (c == null || c.getTaskRecordList() == null) {
            return null;
        }
        List<TaskRecord> recordList = new ArrayList<>();

        List<TaskRecord> taskRecordList = c.getTaskRecordList();
        for (TaskRecord record : taskRecordList) {
            TaskRecord newTaskRecord = new TaskRecord(record.getTeaTask());
            newTaskRecord.setWeekly(record.getWeekly());
            newTaskRecord.setWeek(record.getWeek());
            newTaskRecord.setPitchNum(record.getPitchNum());
            newTaskRecord.setHouseId(record.getHouseId());
            newTaskRecord.setSeat(record.getSeat());
            recordList.add(newTaskRecord);
        }
        Chromosome copy = new Chromosome();
        copy.setTaskRecordList(recordList);
        return copy;
    }

    /**
     * 遗传产生下一代
     * @param p1
     * @param p2
     * @return
     */
    public List<Chromosome> genetic(Chromosome p1, Chromosome p2) {
        if (p1 == null || p2 == null) { //染色体有一个为空，不产生下一代
            return null;
        }
        if (p1.getTaskRecordList() == null || p2.getTaskRecordList() == null) { //染色体有一个没有基因序列，不产生下一代
            return null;
        }

        Chromosome c1 = clone(p1);
        Chromosome c2 = clone(p2);

        arrangeService.genetic(c1.getTaskRecordList(), c2.getTaskRecordList());
        //校验并解决冲突
        arrangeService.verifyClashSolve(c1.getTaskRecordList());
        arrangeService.verifyClashSolve(c2.getTaskRecordList());

        setChromosomeScore(c1);
        setChromosomeScore(c2);

        List<Chromosome> list = new ArrayList<Chromosome>();
        list.add(c1);
        list.add(c2);
        return list;
    }


    public double computeVariance(int[] arr) {
        int len = arr.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += arr[i];
        }
        double avg = sum / len, pow = 0;
        for (int i = 0; i < len; i++) {
            pow += Math.pow(arr[i] - avg, 2);
        }
        return (double) Math.round(Math.sqrt(pow / len) * 100) / 100;
    }

}
