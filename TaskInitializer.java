/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fpabbqos;

import java.util.concurrent.ThreadLocalRandom;

public class TaskInitializer {

    public static void initializeTaskLoads() {
        int step = 900 / SimulationConfig.NUM_VMS;
        double reliabilityStep = 10.0 / (100000 * SimulationConfig.NUM_VMS);

        for (int i = 0; i < SimulationConfig.NUM_VMS; i++) {
            SimulationConfig.speed[i] = 100 + i * step;
            SimulationConfig.vmReliability[i] = 0.0000001 + i * reliabilityStep;
            SimulationConfig.vmCost[i] = 1 + i * step;
        }

        for (int i = 0; i < SimulationConfig.TOTAL_TASKS; i++) {
            if (i < SimulationConfig.TOTAL_TASKS * 0.2) {
                SimulationConfig.taskSizes[i] = 15000 + 20000;
            } else if (i < SimulationConfig.TOTAL_TASKS * 0.6) {
                SimulationConfig.taskSizes[i] = 59000 + 20000;
            } else if (i < SimulationConfig.TOTAL_TASKS * 0.90) {
                SimulationConfig.taskSizes[i] = 101000 + 20000;
            } else if (i < SimulationConfig.TOTAL_TASKS * 0.96) {
                SimulationConfig.taskSizes[i] = 150000 + 120000;
            } else {
                SimulationConfig.taskSizes[i] = 525000 + 200000;
            }
        }
    }

    public static void normalizeBounds() {
        int[] currentCluster = new int[SimulationConfig.CLUSTER_SIZE];
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int c = 0; c < SimulationConfig.CLUSTERS_PER_THREAD; c++) {
            for (int k = 0; k < SimulationConfig.CLUSTER_SIZE; k++) {
                currentCluster[k] = SimulationConfig.taskSizes[k + c * SimulationConfig.CLUSTER_SIZE];
            }

            int[][] population = new int[SimulationConfig.POPULATION_SIZE][SimulationConfig.CLUSTER_SIZE];
            for (int i = 0; i < SimulationConfig.POPULATION_SIZE; i++) {
                for (int j = 0; j < SimulationConfig.CLUSTER_SIZE; j++) {
                    population[i][j] = rand.nextInt(SimulationConfig.NUM_VMS);
                }
            }

            double minMs = Double.MAX_VALUE, maxMs = 0;
            double minCost = Double.MAX_VALUE, maxCost = 0;
            double minRel = Double.MAX_VALUE, maxRel = 0;

            for (int i = 0; i < SimulationConfig.POPULATION_SIZE; i++) {
                double ms  = Optimizers.evaluateClusterMakespan(currentCluster, population[i], 0, SimulationConfig.CLUSTER_SIZE);
                double cost = Optimizers.evaluateClusterCost(currentCluster, population[i], 0, SimulationConfig.CLUSTER_SIZE);
                double rel = Optimizers.evaluateClusterReliability(currentCluster, population[i], 0, SimulationConfig.CLUSTER_SIZE);

                minMs = Math.min(minMs, ms);       maxMs = Math.max(maxMs, ms);
                minCost = Math.min(minCost, cost); maxCost = Math.max(maxCost, cost);
                minRel = Math.min(minRel, rel);     maxRel = Math.max(maxRel, rel);
            }

            SimulationConfig.minMsArr[c] = minMs;       SimulationConfig.maxMsArr[c] = maxMs;
            SimulationConfig.minCostArr[c] = minCost;   SimulationConfig.maxCostArr[c] = maxCost;
            SimulationConfig.minRelArr[c] = minRel;     SimulationConfig.maxRelArr[c] = maxRel;
        }
    }
}