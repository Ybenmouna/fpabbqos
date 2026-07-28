package com.mycompany.fpabbqos;

import java.util.Arrays;
import org.cloudbus.cloudsim.Log;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Initialisation globale des charges de travail et des bornes de normalisation
        TaskInitializer.initializeTaskLoads();
        TaskInitializer.normalizeBounds();

        int[] globalAllocations = new int[SimulationConfig.TOTAL_TASKS];
        double[] results = new double[SimulationConfig.NUM_CLUSTERS];
        int[] flags = new int[SimulationConfig.NUM_CLUSTERS];

        Arrays.fill(globalAllocations, -1);
        Arrays.fill(results, Double.MAX_VALUE);
        Arrays.fill(flags, 1);

        long startTime = System.currentTimeMillis();
        SchedulerWorker[] threads = new SchedulerWorker[2 * SimulationConfig.THREAD_COUNT];

        // Lancement des threads de recherche d'ordonnancement (FPA / Branch & Bound)
        for (int i = 0; i < 2 * SimulationConfig.THREAD_COUNT; i++) {
            int[] threadTasks = new int[SimulationConfig.CLUSTER_SIZE * SimulationConfig.CLUSTERS_PER_THREAD];
            final int startIdx = i / 2 * SimulationConfig.CLUSTER_SIZE * SimulationConfig.CLUSTERS_PER_THREAD;
            for (int j = 0; j < SimulationConfig.CLUSTER_SIZE * SimulationConfig.CLUSTERS_PER_THREAD; j++) {
                threadTasks[j] = SimulationConfig.taskSizes[startIdx + j];
            }
            threads[i] = new SchedulerWorker(i, threadTasks, results, globalAllocations, flags);
            threads[i].start();
        }

        // Attente de la fin d'exécution de tous les threads
        for (int i = 0; i < 2 * SimulationConfig.THREAD_COUNT; i++) {
            threads[i].join();
        }

        SimulationConfig.totalExecutionTime = System.currentTimeMillis() - startTime;

        // =====================================================================
        // AFFICHAGE DES RÉSULTATS D'UNE SEULE SIMULATION
        // =====================================================================
        System.out.println("\n========== OPTIMIZATION RESULTS (SINGLE RUN) ==========");
        for (int i = 0; i < results.length; i++) {
            System.out.println("Cluster " + i + " Objective Function Value : " + results[i]);
            System.out.println("  ├─ Makespan     : " + SimulationConfig.resultMs[i]);
            System.out.println("  ├─ Total Cost   : " + SimulationConfig.resultCost[i]);
            System.out.println("  └─ Reliability  : " + SimulationConfig.resultRel[i]);
        }

        System.out.println("\n========== ALGORITHM COMPARISON ==========");
        System.out.println("Branch and Bound Optimization Score : " + SimulationConfig.optBB);
        System.out.println("Flower Pollination Algorithm Score : " + SimulationConfig.optFPA);

        if (SimulationConfig.optFPA != 0.0) {
            System.out.println("Optimization Ratio (BB / FPA)      : " + (SimulationConfig.optBB / SimulationConfig.optFPA));
        }

        Log.printLine("\nExecution completed in " + SimulationConfig.totalExecutionTime + " ms");
    }
}
