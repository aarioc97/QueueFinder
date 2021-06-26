/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_class;

import java.util.Scanner;
import csp_counter.CSPCounter;
import hybrid_classes.CyclicGASA;
import geneticalgorithm_simulatedannealing.GeneticAlgorithm;
import hybrid_classes.HybridGASA;
import hybrid_classes.HybridSAGA;
import gasa_component.Kromosom;
import gasa_component.Populasi;
import geneticalgorithm_simulatedannealing.SimulatedAnnealing;

/**
 *
 * @author acer
 */
public class QueueFinder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Semua tulisan yang diprint bersifat sementara!!!
        //(termasuk pengubahan tipe mobil dari int ke char)

        Scanner scan = new Scanner(System.in);
        System.out.print("Total mobil yang diproduksi: ");
        int totalMobil = scan.nextInt();
        System.out.print("Jumlah option: ");
        int jmlOption = scan.nextInt();
        System.out.print("Jumlah tipe mobil: ");
        int jmlTipe = scan.nextInt();
        System.out.println("");

        int[] mobilKerja = new int[jmlOption];
        int[] mobilMasuk = new int[jmlOption];

        //Capacity Constraint: mobilKerja : mobilMasuk
        for (int i = 0; i < mobilKerja.length; i++) {
            System.out.print("Mobil yang dapat dikerjakan di workstation " + (i + 1) + ": ");
            mobilKerja[i] = scan.nextInt();
        }
        System.out.println("");
        for (int i = 0; i < mobilMasuk.length; i++) {
            System.out.print("Mobil yang akan masuk ke workstation " + (i + 1) + ": ");
            mobilMasuk[i] = scan.nextInt();
        }
        System.out.println("");

        //karakter untuk tipe mobil
        char[] tipeMobil = new char[jmlTipe];
        for (int i = 0; i < jmlTipe; i++) {
            tipeMobil[i] = (char) (65 + i);
        }

        //untuk tiap tipe, masukkan mobil yang akan diproduksi dan kebutuhan optionnya
        int[] produksiPerTipe = new int[jmlTipe];
        int[][] matriksTipeOption = new int[jmlTipe][jmlOption];
        for (int i = 0; i < jmlTipe; i++) {
            System.out.println("Untuk mobil tipe " + tipeMobil[i] + ": ");
            System.out.print("    Banyaknya mobil yang diproduksi: ");
            int prod = scan.nextInt();
            int tempTotalMobil = totalMobil;
            tempTotalMobil -= prod;
            if (tempTotalMobil >= 0) {
                produksiPerTipe[i] = prod;
            } else {
                System.out.println("JUMLAH MOBIL PER TIPE MELEBIHI TOTAL MOBIL YANG DIPRODUKSI");
                break;
            }
            System.out.print("    Urutan matriks berdasarkan optionnya: ");
            for (int j = 0; j < jmlOption; j++) {
                matriksTipeOption[i][j] = scan.nextInt();
            }
        }
        System.out.println("");

        //Perhitungan Dasar CSP
        CSPCounter csp = new CSPCounter(jmlOption);
        System.out.print("Berdasarkan perhitungan CSP, average utility dari soal tersebut adalah ");
        System.out.println(csp.countCSP(produksiPerTipe, matriksTipeOption, totalMobil, mobilKerja, mobilMasuk));

        System.out.println("");

        //jalankan sisanya dengan genetic algorithm dan simulated annealing

        Populasi pop = new Populasi();
        pop.inisialisasiPopulasi(100, totalMobil, produksiPerTipe);
        Kromosom[] initPop = pop.kromosom;
        
        System.out.println("GENETIC ALGORITHM");
        //Genetic Algorithm
        double gaStart = System.currentTimeMillis();
        
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.getSolution(initPop, mobilKerja, mobilMasuk, matriksTipeOption);
        
        double gaStop = System.currentTimeMillis();

        System.out.println("Urutan yang melebihi capacity constraint: " + ga.populasi.kromTerbaik.gagalDikerjakan);
        System.out.println("Tundaan kerja: " + ga.populasi.kromTerbaik.delay);
        System.out.println("Matriks urutan: ");
        for (int i = 0; i < ga.populasi.kromTerbaik.gen.length; i++) {
            for (int j = 0; j < ga.populasi.kromTerbaik.urutan[i].length; j++) {
                System.out.print(ga.populasi.kromTerbaik.urutan[i][j] + " ");
            }
            System.out.println("");
        }
        
        System.out.println("");
        
        System.out.println("SIMULATED ANNEALING");
        //Simulated Annealing
        double saStart = System.currentTimeMillis();
        
        SimulatedAnnealing sa = new SimulatedAnnealing();
        sa.inisialisasiVariabel(initPop);
        sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);
        
        double saStop = System.currentTimeMillis();
        
        System.out.println("Urutan yang melebihi capacity constraint: " + sa.initialSolution.gagalDikerjakan);
        System.out.println("Tundaan kerja: " + sa.initialSolution.delay);
        System.out.println("Matriks urutan: ");
        for (int i = 0; i < sa.initialSolution.gen.length; i++) {
            for (int j = 0; j < sa.initialSolution.urutan[i].length; j++) {
                System.out.print(sa.initialSolution.urutan[i][j] + " ");
            }
            System.out.println("");
        }
        
        System.out.println("");
        
        System.out.println("HYBRID GA-SA");
        //Hybrid GA-SA
        double gasaStart = System.currentTimeMillis();
        
        HybridGASA gasa = new HybridGASA(initPop, 50);
        gasa.solutionGASA(mobilKerja, mobilMasuk, matriksTipeOption);
        
        double gasaStop = System.currentTimeMillis();

        System.out.println("Urutan yang melebihi capacity constraint: " + gasa.hasilTerbaik.gagalDikerjakan);
        System.out.println("Tundaan kerja: " + gasa.hasilTerbaik.delay);
        System.out.println("Matriks urutan: ");
        for (int i = 0; i < gasa.hasilTerbaik.gen.length; i++) {
            for (int j = 0; j < gasa.hasilTerbaik.urutan[i].length; j++) {
                System.out.print(gasa.hasilTerbaik.urutan[i][j] + " ");
            }
            System.out.println("");
        }

        System.out.println("");

        System.out.println("HYBRID SA-GA");
        //Hybrid SA-GA
        double sagaStart = System.currentTimeMillis();
        
        HybridSAGA saga = new HybridSAGA(initPop, 50);
        saga.solutionSAGA(mobilKerja, mobilMasuk, matriksTipeOption);
        
        double sagaStop = System.currentTimeMillis();

        System.out.println("Urutan yang melebihi capacity constraint: " + saga.hasilTerbaik.gagalDikerjakan);
        System.out.println("Tundaan kerja: " + saga.hasilTerbaik.delay);
        System.out.println("Matriks urutan: ");
        for (int i = 0; i < saga.hasilTerbaik.gen.length; i++) {
            for (int j = 0; j < saga.hasilTerbaik.urutan[i].length; j++) {
                System.out.print(saga.hasilTerbaik.urutan[i][j] + " ");
            }
            System.out.println("");
        }

        System.out.println("");

        System.out.println("CYCLIC GA-SA");
        //Cyclic GA-SA
        double cyclicStart = System.currentTimeMillis();
        
        CyclicGASA cyclic = new CyclicGASA(initPop, 50, 10);
        cyclic.solutionCyclic(mobilKerja, mobilMasuk, matriksTipeOption);
        
        double cyclicStop = System.currentTimeMillis();
        
        System.out.println("Urutan yang melebihi capacity constraint: " + cyclic.hasilTerbaik.gagalDikerjakan);
        System.out.println("Tundaan kerja: " + cyclic.hasilTerbaik.delay);
        System.out.println("Matriks urutan: ");
        for (int i = 0; i < cyclic.hasilTerbaik.gen.length; i++) {
            for (int j = 0; j < cyclic.hasilTerbaik.urutan[i].length; j++) {
                System.out.print(cyclic.hasilTerbaik.urutan[i][j] + " ");
            }
            System.out.println("");
        }
        
        System.out.println("");
        
        System.out.println("Waktu untuk mengerjakan: ");
        System.out.println("Genetic Algorithm: " + ((gaStop - gaStart) / 1000) + " detik");
        System.out.println("Simulated Annealing: " + ((saStop - saStart) / 1000) + " detik");
        System.out.println("Hybrid GA-SA: " + ((gasaStop - gasaStart) / 1000) + " detik");
        System.out.println("Hybrid SA-GA: " + ((sagaStop - sagaStart) / 1000) + " detik");
        System.out.println("Cyclic GA-SA: " + ((cyclicStop - cyclicStart) / 1000) + " detik");
        System.out.println("");
        System.out.println("Total waktu: " + ((cyclicStop - gaStart) / 1000) + " detik");
    }

}
