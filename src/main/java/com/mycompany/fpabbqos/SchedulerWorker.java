/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fpabbqos;

public class SchedulerWorker extends Thread {
    private int threadId;
    private int[] tasks;
    private double[] results;
    private int[] allocations;
    private int[] flags;

    public SchedulerWorker(int threadId, int[] tasks, double[] results, int[] allocations, int[] flags) {
        this.threadId = threadId;
        this.tasks = tasks;
        this.results = results;
        this.allocations = allocations;
        this.flags = flags;
    }

    @Override
    public void run() {
        if (threadId % 2 == 0) {
            long startTime = System.currentTimeMillis();
            Optimizers.FPA((threadId / 2), tasks, results, allocations, flags);
            long executionTime = System.currentTimeMillis() - startTime;
            System.out.println(startTime + " FPA Thread " + threadId + " completed in " + executionTime + " ms");
        } else {
            long startTime = System.currentTimeMillis();
            Optimizers.Branch((threadId / 2), tasks, results, allocations, flags);
            long executionTime = System.currentTimeMillis() - startTime;
            System.out.println(startTime + " Branch Thread " + threadId + " completed in " + executionTime + " ms");
        }

        for (int i = 0; i < results.length; i++) {
            System.out.println(results.length + " Thread " + i + " → " + results[i]);
        }
    }
}