/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fpabbqos;

public class BB_Node implements Comparable<Object> {
    private static int order = 0;
    private int num;
    private int currentTask;
    private double score;
    private double makespan;
    private int[] taskAllocation;
    private int[] vmFinishTimes;

    public BB_Node(int currentTask, double score, double makespan, int[] taskAllocation, int[] vmFinishTimes) {
        order++;
        this.num = order;
        this.currentTask = currentTask;
        this.score = score;
        this.makespan = makespan;
        this.taskAllocation = taskAllocation.clone();
        this.vmFinishTimes = vmFinishTimes.clone();
    }

    public int getNum() { return num; }
    public double getMakespan() { return makespan; }
    public int getCurrentTask() { return currentTask; }
    public int[] getTaskAllocation() { return taskAllocation; }
    public int[] getVmFinishTimes() { return vmFinishTimes; }
    public double getScore() { return score; }

    @Override
    public int compareTo(Object o) {
        BB_Node other = (BB_Node) o;
        int taskDiff = other.currentTask - currentTask;
        if (taskDiff != 0) return taskDiff;

        double msDiff = -other.makespan * 1000000 + makespan * 1000000;
        if (msDiff != 0.0) return (int) msDiff;

        return num - other.num;
    }
}