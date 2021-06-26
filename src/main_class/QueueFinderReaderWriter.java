/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_class;

import csp_counter.CSPCounter;
import gasa_component.Kromosom;
import gasa_component.Populasi;
import geneticalgorithm_simulatedannealing.GeneticAlgorithm;
import geneticalgorithm_simulatedannealing.SimulatedAnnealing;
import hybrid_classes.CyclicGASA;
import hybrid_classes.HybridGASA;
import hybrid_classes.HybridSAGA;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author i15055
 */
public class QueueFinderReaderWriter {

    public static void main(String[] args) {
        
        double waktuProgramStart = System.currentTimeMillis();
        
        String[] baris;
        String line = "";
        String separator = " ";
        int programCounter = 0;
        BufferedReader br;
        BufferedWriter bw;
        File fileinput1 = new File("datainput.txt");
        File fileinput2 = new File("datainputcsplib.txt");

        try {
            System.out.println("Membaca file " + fileinput2.getName() + "...");
            br = new BufferedReader(new FileReader(fileinput2));
            bw = new BufferedWriter(new FileWriter("hasilprogram.txt"));
            System.out.println("");

            while ((line = br.readLine()) != null) {
                programCounter++;
                System.out.println("Soal ke-" + programCounter);
                line = br.readLine();
                baris = line.split(separator);
                int totalMobil = Integer.parseInt(baris[0]);
                System.out.println("Total mobil yang diproduksi: " + totalMobil);
                int jmlOption = Integer.parseInt(baris[1]);
                System.out.println("Jumlah tipe mobil: " + jmlOption);
                int jmlTipe = Integer.parseInt(baris[2]);
                System.out.println("Jumlah tipe mobil: " + jmlTipe);

                System.out.println("");

                int[] mobilKerja = new int[jmlOption];
                int[] mobilMasuk = new int[jmlOption];

                line = br.readLine();
                baris = line.split(separator);
                //Capacity Constraint: mobilKerja : mobilMasuk
                for (int i = 0; i < mobilKerja.length; i++) {
                    mobilKerja[i] = Integer.parseInt(baris[i]);
                    System.out.println("Mobil yang dapat dikerjakan di workstation " + (i + 1) + ": " + mobilKerja[i]);
                }
                System.out.println("");
                line = br.readLine();
                baris = line.split(separator);
                for (int i = 0; i < mobilMasuk.length; i++) {
                    mobilMasuk[i] = Integer.parseInt(baris[i]);
                    System.out.println("Mobil yang akan masuk ke workstation " + (i + 1) + ": " + mobilMasuk[i]);
                }
                System.out.println("");

//            karakter untuk tipe mobil
                char[] tipeMobil = new char[jmlTipe];
                for (int i = 0; i < jmlTipe; i++) {
                    tipeMobil[i] = (char) (65 + i);
                }

                //untuk tiap tipe, masukkan mobil yang akan diproduksi dan kebutuhan optionnya
                int[] produksiPerTipe = new int[jmlTipe];
                int[][] matriksTipeOption = new int[jmlTipe][jmlOption];
                for (int i = 0; i < jmlTipe; i++) {
                    line = br.readLine();
                    baris = line.split(separator);
                    int tempTotalMobil = totalMobil;
                    System.out.println("Untuk mobil tipe " + tipeMobil[Integer.parseInt(baris[0])] + ": ");
                    int prod = Integer.parseInt(baris[1]);
                    System.out.println("    Banyaknya mobil yang diproduksi: " + prod);
                    tempTotalMobil -= prod;
                    if (tempTotalMobil >= 0) {
                        produksiPerTipe[i] = prod;
                    } else {
                        System.out.println("JUMLAH MOBIL PER TIPE MELEBIHI TOTAL MOBIL YANG DIPRODUKSI");
                        break;
                    }
                    System.out.print("    Urutan matriks berdasarkan optionnya: ");
                    for (int j = 0; j < jmlOption; j++) {
                        matriksTipeOption[i][j] = Integer.parseInt(baris[j + 2]);
                        System.out.print(matriksTipeOption[i][j] + " ");
                    }
                    System.out.println("");
                }
                System.out.println("");

                try {
                    System.out.println("Program sedang menjalankan: ");
                    System.out.println("--Perhitungan Dasar CSP--");
                    CSPCounter csp = new CSPCounter(jmlOption);
                    bw.write("Soal ke-" + programCounter);
                    bw.newLine();
                    bw.write("CAR SEQUENCING PROBLEM");
                    bw.newLine();
                    bw.write("Berdasarkan perhitungan CSP, average utility dari soal tersebut adalah " + csp.countCSP(produksiPerTipe, matriksTipeOption, totalMobil, mobilKerja, mobilMasuk));
                    bw.newLine();

                    Populasi pop = new Populasi();
                    pop.inisialisasiPopulasi(50, totalMobil, produksiPerTipe);
                    Kromosom[] initPop = pop.kromosom;

                    System.out.println("--Genetic Algorithm--");
                    //Genetic Algorithm
                    double gaStart = System.currentTimeMillis();

                    GeneticAlgorithm ga = new GeneticAlgorithm();
                    ga.getSolution(initPop, mobilKerja, mobilMasuk, matriksTipeOption);

                    double gaStop = System.currentTimeMillis();

                    bw.newLine();
                    bw.write("GENETIC ALGORITHM");
                    bw.newLine();
                    bw.write("Urutan yang melebihi capacity constraint: " + ga.populasi.kromTerbaik.gagalDikerjakan);
                    bw.newLine();
                    bw.write("Tundaan kerja: " + ga.populasi.kromTerbaik.delay);
                    bw.newLine();
                    bw.write("Matriks urutan: ");
                    bw.newLine();
                    for (int i = 0; i < ga.populasi.kromTerbaik.gen.length; i++) {
                        for (int j = 0; j < ga.populasi.kromTerbaik.urutan[i].length; j++) {
                            bw.write(ga.populasi.kromTerbaik.urutan[i][j] + " ");
                        }
                        bw.newLine();
                    }

                    bw.newLine();

                    System.out.println("--Simulated Annealing--");
                    //Simulated Annealing
                    double saStart = System.currentTimeMillis();

                    SimulatedAnnealing sa = new SimulatedAnnealing();
                    sa.inisialisasiVariabel(initPop);
                    sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);

                    double saStop = System.currentTimeMillis();

                    bw.write("SIMULATED ANNEALING");
                    bw.newLine();
                    bw.write("Urutan yang melebihi capacity constraint: " + sa.initialSolution.gagalDikerjakan);
                    bw.newLine();
                    bw.write("Tundaan kerja: " + sa.initialSolution.delay);
                    bw.newLine();
                    bw.write("Matriks urutan: ");
                    bw.newLine();
                    for (int i = 0; i < sa.initialSolution.gen.length; i++) {
                        for (int j = 0; j < sa.initialSolution.urutan[i].length; j++) {
                            bw.write(sa.initialSolution.urutan[i][j] + " ");
                        }
                        bw.newLine();
                    }

                    bw.newLine();

                    System.out.println("--Hybrid GA-SA--");
                    //Hybrid GA-SA
                    double gasaStart = System.currentTimeMillis();

                    HybridGASA gasa = new HybridGASA(initPop, 25);
                    gasa.solutionGASA(mobilKerja, mobilMasuk, matriksTipeOption);

                    double gasaStop = System.currentTimeMillis();

                    bw.write("HYBRID GA-SA");
                    bw.newLine();
                    bw.write("Urutan yang melebihi capacity constraint: " + gasa.hasilTerbaik.gagalDikerjakan);
                    bw.newLine();
                    bw.write("Tundaan kerja: " + gasa.hasilTerbaik.delay);
                    bw.newLine();
                    bw.write("Matriks urutan: ");
                    bw.newLine();
                    for (int i = 0; i < gasa.hasilTerbaik.gen.length; i++) {
                        for (int j = 0; j < gasa.hasilTerbaik.urutan[i].length; j++) {
                            bw.write(gasa.hasilTerbaik.urutan[i][j] + " ");
                        }
                        bw.newLine();
                    }

                    bw.newLine();

                    System.out.println("--Hybrid SA-GA--");
                    //Hybrid SA-GA
                    double sagaStart = System.currentTimeMillis();

                    HybridSAGA saga = new HybridSAGA(initPop, 25);
                    saga.solutionSAGA(mobilKerja, mobilMasuk, matriksTipeOption);

                    double sagaStop = System.currentTimeMillis();

                    bw.write("HYBRID SA-GA");
                    bw.newLine();
                    bw.write("Urutan yang melebihi capacity constraint: " + saga.hasilTerbaik.gagalDikerjakan);
                    bw.newLine();
                    bw.write("Tundaan kerja: " + saga.hasilTerbaik.delay);
                    bw.newLine();
                    bw.write("Matriks urutan: ");
                    bw.newLine();
                    for (int i = 0; i < saga.hasilTerbaik.gen.length; i++) {
                        for (int j = 0; j < saga.hasilTerbaik.urutan[i].length; j++) {
                            bw.write(saga.hasilTerbaik.urutan[i][j] + " ");
                        }
                        bw.newLine();
                    }

                    bw.newLine();

                    System.out.println("--Cyclic GA-SA--");
                    //Cyclic GA-SA
                    double cyclicStart = System.currentTimeMillis();

                    CyclicGASA cyclic = new CyclicGASA(initPop, 25, 10);
                    cyclic.solutionCyclic(mobilKerja, mobilMasuk, matriksTipeOption);

                    double cyclicStop = System.currentTimeMillis();

                    bw.write("CYCLIC GA-SA");
                    bw.newLine();
                    bw.write("Urutan yang melebihi capacity constraint: " + cyclic.hasilTerbaik.gagalDikerjakan);
                    bw.newLine();
                    bw.write("Tundaan kerja: " + cyclic.hasilTerbaik.delay);
                    bw.newLine();
                    bw.write("Matriks urutan: ");
                    bw.newLine();
                    for (int i = 0; i < cyclic.hasilTerbaik.gen.length; i++) {
                        for (int j = 0; j < cyclic.hasilTerbaik.urutan[i].length; j++) {
                            bw.write(cyclic.hasilTerbaik.urutan[i][j] + " ");
                        }
                        bw.newLine();
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

//                    bw.close();
                    System.out.println("");
                    System.out.println("File hasil (hasilprogram.txt) sudah tertulis.");

                    System.out.println("");
                    bw.newLine();
                } catch (IOException ex) {
                    System.err.println("File tidak dapat tertulis.");
                }
            }
            bw.close();
        } catch (IOException ex) {
            System.err.println("File tidak ditemukan.");
        }
        
        double waktuProgramStop = System.currentTimeMillis();
        System.out.println("Total waktu yang dihabiskan program: " + ((waktuProgramStop - waktuProgramStart) / 1000) + " detik");
        
    }
}
