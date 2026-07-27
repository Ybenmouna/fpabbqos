/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fpabbqos;
 
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;



  class CustomVm extends Vm {

    private double vit;   // Vitesse (utilisée comme MIPS)
    private double fia;   // Fiabilité
    private double cost;  // Coût

    public CustomVm(
            int id, int userId,
            double vit,
            int numberOfPes, int ram, long bw, long size,
            String vmm, CloudletScheduler scheduler,
            double fia, double cost) {

        super(id, userId, vit, numberOfPes, ram, bw, size, vmm, scheduler);
        this.vit = vit;
        this.fia = fia;
        this.cost = cost;
    }

    public double getVit() { return vit; }
    public double getFia() { return fia; }
    public double getCost() { return cost; }
}