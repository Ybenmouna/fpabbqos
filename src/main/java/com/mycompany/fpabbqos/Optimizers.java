/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fpabbqos;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public class Optimizers {

    public static double evaluateClusterMakespan(int[] taskSizes, int[] solution, int start, int end) {
        double[] vmLoad = new double[SimulationConfig.NUM_VMS];
        for (int i = start; i < end; i++) {
            int vmId = solution[i];
            if (vmId != -1) {
                double execTime = (double) taskSizes[i] / SimulationConfig.speed[vmId];
                vmLoad[vmId] += execTime;
            }
        }
        double makespan = 0.0;
        for (double load : vmLoad) {
            makespan = Math.max(makespan, load);
        }
        return makespan;
    }

    public static double evaluateClusterCost(int[] taskSizes, int[] solution, int start, int end) {
        double cost = 0.0;
        for (int i = start; i < end; i++) {
            int vmId = solution[i];
            if (vmId != -1) {
                double execTime = (double) taskSizes[i] / SimulationConfig.speed[vmId];
                cost += SimulationConfig.vmCost[vmId] * execTime;
            }
        }
        return cost;
    }

    public static double evaluateClusterReliability(int[] taskSizes, int[] solution, int start, int end) {
        double reliability = 1.0;
        for (int i = start; i < end; i++) {
            int vmId = solution[i];
            if (vmId != -1) {
                double execTime = (double) taskSizes[i] / SimulationConfig.speed[vmId];
                reliability *= Math.exp(-SimulationConfig.vmReliability[vmId] * execTime);
            }
        }
        return reliability;
    }

    public static double evaluateCluster(
            int[] taskSizes, int[] solution, int start, int end,
            double minMs, double maxMs, double minCost, double maxCost, double minRel, double maxRel
    ) {
        double msVal   = evaluateClusterMakespan(taskSizes, solution, start, end);
        double costVal = evaluateClusterCost(taskSizes, solution, start, end);
        double relVal  = evaluateClusterReliability(taskSizes, solution, start, end);

        double normalizedMs   = (maxMs > minMs)     ? (msVal - minMs) / (maxMs - minMs) : 0.0;
        double normalizedCost = (maxCost > minCost) ? (costVal - minCost) / (maxCost - minCost) : 0.0;
        double normalizedRel  = (maxRel > minRel)   ? (relVal - minRel) / (maxRel - minRel) : 0.0;

        normalizedMs   = Math.max(0.0, Math.min(1.0, normalizedMs));
        normalizedCost = Math.max(0.0, Math.min(1.0, normalizedCost));
        normalizedRel  = Math.max(0.0, Math.min(1.0, normalizedRel));

        return SimulationConfig.W1 * normalizedMs + SimulationConfig.W2 * normalizedCost + SimulationConfig.W3 * (1.0 - normalizedRel);
    }

    public static int levyFlight() {
        Random rand = new Random();
        double step = rand.nextGaussian();
        return (int) Math.round(step) % SimulationConfig.NUM_VMS;
    }

    public static void Branch(int threadId, int[] tasks, double[] results, int[] allocations, int[] flags) {
        int[] currentCluster = new int[SimulationConfig.CLUSTER_SIZE];
        for (int c = 0; c < SimulationConfig.CLUSTERS_PER_THREAD; c++) {
            for (int k = 0; k < SimulationConfig.CLUSTER_SIZE; k++) {
                currentCluster[k] = SimulationConfig.taskSizes[k + c * SimulationConfig.CLUSTER_SIZE];
            }
            TreeSet<BB_Node> searchTree = new TreeSet<>();
            int[] taskAllocation = new int[SimulationConfig.CLUSTER_SIZE];
            int[] nextTaskAllocation = new int[SimulationConfig.CLUSTER_SIZE];
            int[] vmFinishTimes = new int[SimulationConfig.NUM_VMS];

            Arrays.fill(taskAllocation, -1);
            Arrays.fill(nextTaskAllocation, -1);

            double bound = 0.0;
            int currentTask = 0;
            BB_Node rootNode = new BB_Node(currentTask, 0.0, bound, taskAllocation, vmFinishTimes);
            searchTree.add(rootNode);

            int vmIndex;
            while ((!searchTree.isEmpty()) && (flags[threadId * SimulationConfig.CLUSTERS_PER_THREAD + c] == 1)) {
                Arrays.fill(nextTaskAllocation, -1);
                taskAllocation = searchTree.first().getTaskAllocation();
                bound = searchTree.first().getMakespan();

                if (bound < results[c + SimulationConfig.CLUSTERS_PER_THREAD * threadId]) {
                    currentTask = searchTree.first().getCurrentTask();
                    Iterator<BB_Node> iter = searchTree.iterator();
                    iter.next();
                    iter.remove();

                    vmIndex = 0;
                    while (vmIndex < SimulationConfig.NUM_VMS) {
                        nextTaskAllocation = taskAllocation.clone();
                        nextTaskAllocation[currentTask] = vmIndex;

                        bound = evaluateCluster(currentCluster, nextTaskAllocation, 0, SimulationConfig.CLUSTER_SIZE,
                                SimulationConfig.minMsArr[c], SimulationConfig.maxMsArr[c],
                                SimulationConfig.minCostArr[c], SimulationConfig.maxCostArr[c],
                                SimulationConfig.minRelArr[c], SimulationConfig.maxRelArr[c]);

                        if (((currentTask + 1) == SimulationConfig.CLUSTER_SIZE)) {
                            int resIndex = c + SimulationConfig.CLUSTERS_PER_THREAD * threadId;
                            int baseAllocIndex = c * SimulationConfig.CLUSTER_SIZE + threadId * SimulationConfig.CLUSTER_SIZE * SimulationConfig.CLUSTERS_PER_THREAD;

                            synchronized (results) {
                                if ((bound < results[resIndex]) && (bound > 0.0)) {
                                    SimulationConfig.resultMs[resIndex] = evaluateClusterMakespan(currentCluster, nextTaskAllocation, 0, SimulationConfig.CLUSTER_SIZE);
                                    SimulationConfig.resultCost[resIndex] = evaluateClusterCost(currentCluster, nextTaskAllocation, 0, SimulationConfig.CLUSTER_SIZE);
                                    SimulationConfig.resultRel[resIndex] = evaluateClusterReliability(currentCluster, nextTaskAllocation, 0, SimulationConfig.CLUSTER_SIZE);

                                    if (results[resIndex] != Double.MAX_VALUE) {
                                        SimulationConfig.optBB += results[resIndex] - bound;
                                    }
                                    results[resIndex] = bound;
                                    System.arraycopy(nextTaskAllocation, 0, allocations, baseAllocIndex, SimulationConfig.CLUSTER_SIZE);
                                }
                            }
                        } else {
                            if ((bound < results[c + SimulationConfig.CLUSTERS_PER_THREAD * threadId])) {
                                BB_Node newNode = new BB_Node(currentTask + 1, 0, bound, nextTaskAllocation, vmFinishTimes);
                                searchTree.add(newNode);
                            }
                        }
                        vmIndex++;
                    }
                }
            }
        }
    }

    public static void FPA(int threadId, int[] tasks, double[] results, int[] allocations, int[] flags) {
        int[] currentCluster = new int[SimulationConfig.CLUSTER_SIZE];
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int c = 0; c < SimulationConfig.CLUSTERS_PER_THREAD; c++) {
            for (int k = 0; k < SimulationConfig.CLUSTER_SIZE; k++) {
                currentCluster[k] = SimulationConfig.taskSizes[k + c * SimulationConfig.CLUSTER_SIZE];
            }
            int[][] population = new int[SimulationConfig.POPULATION_SIZE][SimulationConfig.CLUSTER_SIZE];
            for (int i = 0; i < SimulationConfig.POPULATION_SIZE; i++)
                for (int j = 0; j < SimulationConfig.CLUSTER_SIZE; j++)
                    population[i][j] = rand.nextInt(SimulationConfig.NUM_VMS);

            double[] fitness = new double[SimulationConfig.POPULATION_SIZE];
            for (int i = 0; i < SimulationConfig.POPULATION_SIZE; i++) {
                fitness[i] = evaluateCluster(currentCluster, population[i], 0, SimulationConfig.CLUSTER_SIZE,
                        SimulationConfig.minMsArr[c], SimulationConfig.maxMsArr[c],
                        SimulationConfig.minCostArr[c], SimulationConfig.maxCostArr[c],
                        SimulationConfig.minRelArr[c], SimulationConfig.maxRelArr[c]);
            }

            int[] bestSolution = population[0].clone();
            double bestFitness = fitness[0];
            for (int i = 1; i < SimulationConfig.POPULATION_SIZE; i++) {
                if (fitness[i] < bestFitness) {
                    bestFitness = fitness[i];
                    bestSolution = population[i].clone();
                }
            }

            for (int gen = 0; gen < SimulationConfig.MAX_ITERATIONS; gen++) {
                for (int i = 0; i < SimulationConfig.POPULATION_SIZE; i++) {
                    int[] newSolution = population[i].clone();

                    if (rand.nextDouble() < 0.5) {
                        int j = rand.nextInt(SimulationConfig.POPULATION_SIZE);
                        int k = rand.nextInt(SimulationConfig.POPULATION_SIZE);
                        int swapIdx = rand.nextInt(SimulationConfig.CLUSTER_SIZE);
                        newSolution[swapIdx] = (rand.nextDouble() > 0.5) ? population[j][swapIdx] : population[k][swapIdx];
                    } else {
                        int move = levyFlight();
                        int swapIdx = rand.nextInt(SimulationConfig.CLUSTER_SIZE);
                        newSolution[swapIdx] = Math.floorMod(newSolution[swapIdx] + move, SimulationConfig.NUM_VMS);
                    }

                    double newFitness = evaluateCluster(currentCluster, newSolution, 0, SimulationConfig.CLUSTER_SIZE,
                            SimulationConfig.minMsArr[c], SimulationConfig.maxMsArr[c],
                            SimulationConfig.minCostArr[c], SimulationConfig.maxCostArr[c],
                            SimulationConfig.minRelArr[c], SimulationConfig.maxRelArr[c]);

                    if (newFitness < fitness[i]) {
                        population[i] = newSolution.clone();
                        fitness[i] = newFitness;

                        int resIndex = c + SimulationConfig.CLUSTERS_PER_THREAD * threadId;
                        int baseAllocIndex = c * SimulationConfig.CLUSTER_SIZE + threadId * SimulationConfig.CLUSTER_SIZE * SimulationConfig.CLUSTERS_PER_THREAD;

                        synchronized (results) {
                            if ((newFitness <= results[resIndex]) && (newFitness > 0.0)) {
                                SimulationConfig.resultMs[resIndex] = evaluateClusterMakespan(currentCluster, population[i], 0, SimulationConfig.CLUSTER_SIZE);
                                SimulationConfig.resultCost[resIndex] = evaluateClusterCost(currentCluster, newSolution, 0, SimulationConfig.CLUSTER_SIZE);
                                SimulationConfig.resultRel[resIndex] = evaluateClusterReliability(currentCluster, newSolution, 0, SimulationConfig.CLUSTER_SIZE);
                                if (results[resIndex] != Double.MAX_VALUE) SimulationConfig.optFPA += results[resIndex] - newFitness;

                                results[resIndex] = newFitness;
                                System.arraycopy(population[i], 0, allocations, baseAllocIndex, SimulationConfig.CLUSTER_SIZE);
                            }
                            bestSolution = newSolution.clone();
                            bestFitness = newFitness;
                        }
                    }
                }
            }
            flags[threadId * SimulationConfig.CLUSTERS_PER_THREAD + c] = 0;
        }
    }
}