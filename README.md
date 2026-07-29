
# FPABBQoS (Flower Pollination Algorithm & Branch and Bound for QoS Cloud Scheduling)

FPABBQoS is a high-performance Java framework designed to tackle task scheduling and resource allocation challenges in Cloud Computing environments. The system optimizes Quality of Service (QoS) parameters; specifically **Makespan**, **Execution Cost**, and **System Reliability**; by combining metaheuristic search (Flower Pollination Algorithm) with exact tree-pruning strategies (Branch and Bound).

---

## 🏗 Architecture & Design Principles

The architecture of FPABBQoS follows a modular, decoupled pattern combining CloudSim simulation capabilities with a hybrid optimization pipeline:

```text
               +---------------------------------------------------+
               |                    Main.java                      |
               |       (Orchestration & Workflow Control)          |
               +-------------------------+-------------------------+
                                         |
         +-------------------------------+-------------------------------+
         |                                                               |
+--------v------------------+                                 +----------v---------------+
|   SimulationConfig.java   |                                 |   TaskInitializer.java   |
| (Hosts, Datacenters, VMs) |                                 |  (QoS Tasks & Cloudlets) |
+--------+------------------+                                 +----------+---------------+
         |                                                               |
         +-------------------------------+-------------------------------+
                                         |
                               +---------v---------+
                               | CloudSimManager   |
                               +---------+---------+
                                         |
                               +---------v---------+
                               |    Optimizers     |
                               | (FPA + B&B Engine)|
                               +----+---------+----+
                                    |         |
                  +-----------------+         +-----------------+
                  |                                             |
        +---------v---------+                         +---------v---------+
        |     BB_Node       |                         |  SchedulerWorker  |
        | (Tree Exploration |                         | (Bridge between   |
        |   & Bounding)     |                         |  FPA & B&B Exec)  |
        +-------------------+                         +---------+---------+
                                                                |
                                                      +---------v---------+
                                                      |     CustomVm      |
                                                      | (Metrics Tracking)|
                                                      +-------------------+
```

### Core Architecture Components:
1. **Simulation & Configuration Engine:**
   * `SimulationConfig.java`: Centralizes resource specifications (CPU MIPS, RAM, bandwidth, cost parameters) for Data Centers, Hosts, and Virtual Machines.
   * `TaskInitializer.java`: Generates workload tasks (cloudlets) associated with specific CPU requirements, deadlines, and QoS constraints.
   * `CloudSimManager.java`: Manages the lifecycle of CloudSim simulation entities and handles event processing.

2. **Hybrid Optimization Pipeline (`Optimizers.java` & `BB_Node.java`):**
   * **Flower Pollination Algorithm (FPA):** Performs global exploration to find promising regions of the schedule space.
   * **Branch and Bound (B&B) Engine (`BB_Node.java`):** Represents search tree nodes, performing lower-bound calculations and pruning non-promising branches to refine allocation.

3. **Execution & Metric Tracking:**
   * `SchedulerWorker.java`: Coordinates worker threads to manage task-to-VM mapping, acting as the execution bridge between the Flower Pollination Algorithm (FPA) global exploration and Branch and Bound (B&B) local optimization/refinement.
   * `CustomVm.java`: An extended CloudSim VM implementation that tracks real-time execution statistics, utilization, and QoS metrics.

---
 

## 📌 Table of Contents
- [Overview](#-overview)
- [Project Structure](#-project-structure)
- [Key Classes & Modules](#-key-classes--modules)
- [Prerequisites](#-prerequisites)
- [Installation & Build](#-installation--build)
- [Execution](#-execution)
- [Author](#-author)

---

## 📖 Overview

Efficient task scheduling in Cloud Computing requires optimizing multiple Quality of Service (QoS) criteria (Makespan, Execution Cost, and System Reliability) while respecting system constraints. 

**FPABBQoS** addresses this problem through a hybrid optimization approach:
* **Flower Pollination Algorithm (FPA):** Used for global exploration of the decision space.
* **Branch and Bound (B&B):** Used for exact tree search, bounding, and pruning candidate solutions (`BB_Node`).
* **CloudSim Platform:** Simulates data centers, hosts, virtual machines (`CustomVm`), and task workloads.

---

## 📂 Project Structure

```text
fpabbqos/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── mycompany/
                    └── fpabbqos/
                        ├── Main.java
                        ├── SimulationConfig.java
                        ├── TaskInitializer.java
                        ├── CloudSimManager.java
                        ├── Optimizers.java
                        ├── SchedulerWorker.java
                        ├── CustomVm.java
                        └── BB_Node.java
```

---

## 🧩 Key Classes & Modules

* **`Main.java`**: Main entry point for starting and orchestrating the simulation pipeline.
* **`SimulationConfig.java`**: Holds simulation settings for data centers, host specs, and virtual machines.
* **`TaskInitializer.java`**: Generates and configures cloudlets/tasks with specific workloads and QoS constraints.
* **`CloudSimManager.java`**: Handles the CloudSim engine lifecycle and entity creation.
* **`Optimizers.java`**: Core algorithm logic implementing FPA and Branch & Bound exploration.
* **`BB_Node.java`**: Data structure representing search tree nodes in the Branch and Bound algorithm.
* **`SchedulerWorker.java`**: Coordinates worker threads to manage task-to-VM mapping, serving as the bridge between FPA (global exploration) and Branch and Bound (B&B execution/refinement).
* **`CustomVm.java`**: Extended VM class with specialized tracking for QoS metrics.

---

## 🛠 Prerequisites

* **Java Development Kit (JDK 11 or higher)**
* **Apache Maven 3.6+**
* **Git**

---

## ⚙️ Installation & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Ybenmouna/fpabbqos.git
   cd fpabbqos
   ```

2. **Compile and package the project:**
   ```bash
   mvn clean package
   ```

---

## 🚀 Execution

Run the simulation using Maven:

```bash
mvn exec:java -Dexec.mainClass="com.mycompany.fpabbqos.Main"
```

Or run the compiled executable JAR file directly:

```bash
java -jar target/fpabbqos-1.0-SNAPSHOT.jar
```

---

## 👤 Author

* **Youcef BENMOUNA** - [Ybenmouna](https://github.com/Ybenmouna)
readme.md
