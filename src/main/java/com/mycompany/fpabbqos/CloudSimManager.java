/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fpabbqos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class CloudSimManager {

    public static List<Vm> createVM(int userId, int numVms) {
        List<Vm> createdVmList = new ArrayList<>();
        double step = 900.0 / numVms;
        double reliabilityStep = 10.0 / (100000 * numVms);

        for (int i = 0; i < numVms; i++) {
            double speed = 100 + i * 10 * (step / 10);
            double reliability = Math.min(1.0, 0.0000001 + i * 10 * (reliabilityStep / 10));
            double cost = 1 + i * 10 * (step / 10);

            Vm vm = new CustomVm(
                    i, userId, speed, 1, 2048, 10000, 100000, "Xen",
                    new CloudletSchedulerSpaceShared(), reliability, cost
            );
            createdVmList.add(vm);
        }
        return createdVmList;
    }

    public static List<Cloudlet> createCloudletFromTaskSizes(int userId, int cloudletCount, int[] taskSizes, int clusterIndex) {
        LinkedList<Cloudlet> list = new LinkedList<>();
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudletCount; i++) {
            long length = taskSizes[i + clusterIndex * SimulationConfig.CLUSTER_SIZE];
            Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(userId);
            list.add(cloudlet);
        }
        return list;
    }

    public static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        int mips = 400000;
        int ram = 8192000;
        long storage = 1000000000;
        int bw = 100000;
        int numberOfHosts = 8;
        int pesPerHost = 8;

        for (int hostId = 0; hostId < numberOfHosts; hostId++) {
            List<Pe> peList = new ArrayList<>();
            for (int peId = 0; peId < pesPerHost; peId++) {
                peList.add(new Pe(peId, new PeProvisionerSimple(mips)));
            }
            Host host = new Host(
                    hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw),
                    storage, peList, new VmSchedulerTimeShared(peList)
            );
            hostList.add(host);
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.1, 0.1
        );

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }
}