/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_classes;

import java.util.Scanner;
import csp_counter.CSPCounter;
import hybrid_algorithms.CyclicGASA;
import elementary_algorithms.GeneticAlgorithm;
import hybrid_algorithms.HybridGASA;
import hybrid_algorithms.HybridSAGA;
import elementary_components.Kromosom;
import elementary_components.Populasi;
import elementary_algorithms.SimulatedAnnealing;

/**
 *
 * @author acer
 */
public class QueueFinder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        System.out.print("Total mobil yang diproduksi: ");
        int totalMobil = scan.nextInt();
        if (totalMobil <= 0) {
            System.out.println("TIDAK ADA MOBIL YANG DIURUTKAN");
            System.exit(0);
        }
        System.out.print("Jumlah option: ");
        int jmlOption = scan.nextInt();
        if (jmlOption <= 0) {
            System.out.println("JUMLAH OPTION TIDAK VALID");
            System.exit(0);
        }
        System.out.print("Jumlah tipe mobil: ");
        int jmlTipe = scan.nextInt();
        if (jmlTipe <= 0) {
            System.out.println("JUMLAH TIPE TIDAK VALID");
            System.exit(0);
        }
        System.out.println("");

        int[] mobilKerja = new int[jmlOption];
        int[] mobilMasuk = new int[jmlOption];

        //Capacity Constraint: mobilKerja : mobilMasuk
        for (int i = 0; i < mobilKerja.length; i++) {
            System.out.print("Mobil yang dapat dikerjakan di workstation " + (i + 1) + ": ");
            mobilKerja[i] = scan.nextInt();
            if (mobilKerja[i] == 0) {
                System.out.println("CAPACITY CONSTRAINT TIDAK VALID (MOBIL YANG DIKERJAKAN = 0)");
                System.exit(0);
            }
        }
        System.out.println("");
        for (int i = 0; i < mobilMasuk.length; i++) {
            System.out.print("Mobil yang akan masuk ke workstation " + (i + 1) + ": ");
            mobilMasuk[i] = scan.nextInt();
            if (mobilMasuk[i] == 0) {
                System.out.println("CAPACITY CONSTRAINT TIDAK VALID (MOBIL YANG MASUK = 0)");
                System.exit(0);
            }
        }
        System.out.println("");

        //karakter untuk tipe mobil
        String[] tipeMobil = new String[jmlTipe];
        int a = 0;
        int b = 0;
        int c = 0;
        for (int i = 0; i < jmlTipe; i++) {
            if (a > 25) {
                tipeMobil[i] = String.valueOf((char) (65 + b)) + String.valueOf((char) (65 + c));
                c++;
                if (a > 50) {
                    b++;
                }
            } else {
                tipeMobil[i] = String.valueOf((char) (65 + a));
                a++;
            }
        }

        //untuk tiap tipe, masukkan mobil yang akan diproduksi dan kebutuhan optionnya
        int[] produksiPerTipe = new int[jmlTipe];
        int[][] matriksTipeOption = new int[jmlTipe][jmlOption];
        int tempTotalMobil = totalMobil;
        for (int i = 0; i < jmlTipe; i++) {
            System.out.println("Untuk mobil tipe " + tipeMobil[i] + ": ");
            System.out.print("    Banyaknya mobil yang diproduksi: ");
            int prod = scan.nextInt();
            tempTotalMobil -= prod;
            if (tempTotalMobil < 0) {
                System.out.println("JUMLAH MOBIL PER TIPE MELEBIHI TOTAL MOBIL YANG DIPRODUKSI");
                System.exit(0);
            } else if (prod == 0) {
                System.out.println("TERDAPAT TIPE MOBIL YANG TIDAK DIPRODUKSI");
                System.exit(0);
            }
            produksiPerTipe[i] = prod;
            System.out.print("    Urutan matriks berdasarkan optionnya: ");
            for (int j = 0; j < jmlOption; j++) {
                matriksTipeOption[i][j] = scan.nextInt();
            }
        }
        System.out.println("");

        //Perhitungan Dasar CSP
        CSPCounter csp = new CSPCounter(jmlOption);
        String avgUtil = csp.countCSP(produksiPerTipe, matriksTipeOption, totalMobil, mobilKerja, mobilMasuk);
        if (Double.parseDouble(avgUtil) == 0) {
            System.out.println("SEMUA OPTION TIDAK TERPAKAI");
            System.exit(0);
        }
        System.out.print("Berdasarkan perhitungan CSP, average utility dari soal tersebut adalah " + avgUtil);

        System.out.println("");

        //jalankan sisanya dengan genetic algorithm dan simulated annealing
        Populasi pop = new Populasi();
        pop.inisialisasiPopulasi(1000, totalMobil, produksiPerTipe);
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

        HybridGASA gasa = new HybridGASA(initPop, 500);
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

        HybridSAGA saga = new HybridSAGA(initPop, 500);
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

        CyclicGASA cyclic = new CyclicGASA(initPop, 500, 10);
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
