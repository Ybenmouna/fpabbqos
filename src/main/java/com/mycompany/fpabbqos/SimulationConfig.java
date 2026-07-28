/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fpabbqos;

public class SimulationConfig {
    public static final int CLUSTER_SIZE = 200;
    public static final int NUM_CLUSTERS = 1;
    public static final int TOTAL_TASKS = CLUSTER_SIZE * NUM_CLUSTERS;
    public static final int THREAD_COUNT = 1;
    public static final int NUM_VMS = 60;

    // Weights for fitness function
    public static final double W1 = 0.6; // Makespan Weight
    public static final double W2 = 0.2; // Cost Weight
    public static final double W3 = 0.2; // Reliability Weight

    public static final int POPULATION_SIZE = 100;
    public static final int MAX_ITERATIONS = 400;
    public static final int CLUSTERS_PER_THREAD = NUM_CLUSTERS / THREAD_COUNT;

    // Shared global arrays
    public static int[] taskSizes = new int[TOTAL_TASKS];
    public static double[] speed = new double[NUM_VMS];
    public static double[] vmCost = new double[NUM_VMS];
    public static double[] vmReliability = new double[NUM_VMS];

    public static double[] minMsArr = new double[CLUSTERS_PER_THREAD];
    public static double[] maxMsArr = new double[CLUSTERS_PER_THREAD];
    public static double[] minCostArr = new double[CLUSTERS_PER_THREAD];
    public static double[] maxCostArr = new double[CLUSTERS_PER_THREAD];
    public static double[] minRelArr = new double[CLUSTERS_PER_THREAD];
    public static double[] maxRelArr = new double[CLUSTERS_PER_THREAD];

    public static double[] resultMs = new double[NUM_CLUSTERS];
    public static double[] resultCost = new double[NUM_CLUSTERS];
    public static double[] resultRel = new double[NUM_CLUSTERS];

    public static double optFPA = 0.0;
    public static double optBB = 0.0;
    public static double globalFPA = 0.0;
    public static double globalBB = 0.0;
    public static double totalSum = 0.0;
    public static long totalExecutionTime;
}