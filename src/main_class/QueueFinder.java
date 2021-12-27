/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_class;

import java.util.Scanner;
import csp_counter.CSPCounter;
import hybrid_algorithms.CyclicGASA;
import elementary_algorithms.GeneticAlgorithm;
import hybrid_algorithms.HybridGASA;
import hybrid_algorithms.HybridSAGA;
import elementary_components.Kromosom;
import elementary_components.Populasi;
import elementary_algorithms.SimulatedAnnealing;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;

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
        System.out.println("Terdapat dua format input dan output");
        System.out.println("1. Manual (console)");
        System.out.println("2. Otomatis (file)");
        System.out.println("");
        System.out.print("Pilih format yang akan dipakai: ");

        try {
            int pilihanFormat = scan.nextInt();

            switch (pilihanFormat) {
                case 1: {
                    System.out.println("");
                    System.out.println("Format manual (via console)");
                    CSP csp = new CSP();
                    csp.waktuProgramStart = System.currentTimeMillis();
                    System.out.print("Total mobil yang diproduksi: ");
                    csp.totalMobil = scan.nextInt();
                    if (csp.totalMobil <= 0) {
                        System.out.println("TIDAK ADA MOBIL YANG DIURUTKAN");
                        System.exit(0);
                    }
                    System.out.print("Jumlah option: ");
                    csp.jmlOption = scan.nextInt();
                    if (csp.jmlOption <= 0) {
                        System.out.println("JUMLAH OPTION TIDAK VALID");
                        System.exit(0);
                    }
                    System.out.print("Jumlah tipe mobil: ");
                    csp.jmlTipe = scan.nextInt();
                    if (csp.jmlTipe <= 0) {
                        System.out.println("JUMLAH TIPE TIDAK VALID");
                        System.exit(0);
                    }
                    System.out.println("");
                    csp.mobilKerja = new int[csp.jmlOption];
                    csp.mobilMasuk = new int[csp.jmlOption];
                    //Capacity Constraint: mobilKerja : mobilMasuk
                    for (int i = 0; i < csp.mobilKerja.length; i++) {
                        System.out.print("Mobil yang dapat dikerjakan di workstation " + (i + 1) + ": ");
                        csp.mobilKerja[i] = scan.nextInt();
                        if (csp.mobilKerja[i] == 0) {
                            System.out.println("CAPACITY CONSTRAINT TIDAK VALID (MOBIL YANG DIKERJAKAN = 0)");
                            System.exit(0);
                        }
                    }
                    System.out.println("");
                    for (int i = 0; i < csp.mobilMasuk.length; i++) {
                        System.out.print("Mobil yang akan masuk ke workstation " + (i + 1) + ": ");
                        csp.mobilMasuk[i] = scan.nextInt();
                        if (csp.mobilMasuk[i] == 0) {
                            System.out.println("CAPACITY CONSTRAINT TIDAK VALID (MOBIL YANG MASUK = 0)");
                            System.exit(0);
                        }
                    }
                    System.out.println("");
                    //            karakter untuk tipe mobil
                    csp.tipeMobil = new String[csp.jmlTipe];
                    int a = 0;
                    int b = 0;
                    int c = 0;
                    for (int i = 0; i < csp.jmlTipe; i++) {
                        if (a > 25) {
                            csp.tipeMobil[i] = String.valueOf((char) (65 + b)) + String.valueOf((char) (65 + c));
                            c++;
                            if (a > 50) {
                                b++;
                            }
                        } else {
                            csp.tipeMobil[i] = String.valueOf((char) (65 + a));
                            a++;
                        }
                    }       //untuk tiap tipe, masukkan mobil yang akan diproduksi dan kebutuhan optionnya
                    csp.produksiPerTipe = new int[csp.jmlTipe];
                    csp.matriksTipeOption = new int[csp.jmlTipe][csp.jmlOption];
                    csp.tempTotalMobil = csp.totalMobil;
                    for (int i = 0; i < csp.jmlTipe; i++) {
                        System.out.println("Untuk mobil tipe " + csp.tipeMobil[i] + ": ");
                        System.out.print("    Banyaknya mobil yang diproduksi: ");
                        csp.prod = scan.nextInt();
                        csp.tempTotalMobil -= csp.prod;
                        if (csp.tempTotalMobil < 0) {
                            System.out.println("JUMLAH MOBIL PER TIPE MELEBIHI TOTAL MOBIL YANG DIPRODUKSI");
                            System.exit(0);
                        } else if (csp.prod == 0) {
                            System.out.println("TERDAPAT TIPE MOBIL YANG TIDAK DIPRODUKSI");
                            System.exit(0);
                        }
                        csp.produksiPerTipe[i] = csp.prod;
                        System.out.print("    Urutan matriks berdasarkan optionnya: ");
                        for (int j = 0; j < csp.jmlOption; j++) {
                            csp.matriksTipeOption[i][j] = scan.nextInt();
                        }
                    }
                    System.out.println("");
                    //Perhitungan Dasar CSP
                    CSPCounter cspCount = new CSPCounter(csp.jmlOption);
                    String avgUtil = cspCount.countCSP(csp.produksiPerTipe, csp.matriksTipeOption, csp.totalMobil, csp.mobilKerja, csp.mobilMasuk);
                    if (Double.parseDouble(avgUtil) == 0) {
                        System.out.println("SEMUA OPTION TIDAK TERPAKAI");
                        System.exit(0);
                    }
                    System.out.print("Berdasarkan perhitungan CSP, average utility dari soal tersebut adalah " + avgUtil);
                    System.out.println("");
                    //jalankan sisanya dengan genetic algorithm dan simulated annealing
                    Populasi pop = new Populasi();
                    pop.inisialisasiPopulasi(1000, csp.totalMobil, csp.produksiPerTipe);
                    Kromosom[] initPop = pop.kromosom;
                    System.out.println("GENETIC ALGORITHM");
                    //Genetic Algorithm
                    csp.gaStart = System.currentTimeMillis();
                    GeneticAlgorithm ga = new GeneticAlgorithm();
                    ga.getSolution(initPop, csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);
                    csp.gaStop = System.currentTimeMillis();
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
                    csp.saStart = System.currentTimeMillis();
                    SimulatedAnnealing sa = new SimulatedAnnealing();
                    sa.inisialisasiVariabel(initPop);
                    sa.getSolution(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);
                    csp.saStop = System.currentTimeMillis();
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
                    csp.gasaStart = System.currentTimeMillis();
                    HybridGASA gasa = new HybridGASA(initPop, 500);
                    gasa.solutionGASA(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);
                    csp.gasaStop = System.currentTimeMillis();
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
                    csp.sagaStart = System.currentTimeMillis();
                    HybridSAGA saga = new HybridSAGA(initPop, 500);
                    saga.solutionSAGA(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);
                    csp.sagaStop = System.currentTimeMillis();
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
                    csp.cyclicStart = System.currentTimeMillis();
                    CyclicGASA cyclic = new CyclicGASA(initPop, 500, 10);
                    cyclic.solutionCyclic(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);
                    csp.cyclicStop = System.currentTimeMillis();
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
                    System.out.println("Genetic Algorithm: " + csp.countGATime() + " detik");
                    System.out.println("Simulated Annealing: " + csp.countSATime() + " detik");
                    System.out.println("Hybrid GA-SA: " + csp.countGASATime() + " detik");
                    System.out.println("Hybrid SA-GA: " + csp.countSAGATime() + " detik");
                    System.out.println("Cyclic GA-SA: " + csp.countCyclicTime() + " detik");
                    System.out.println("");
                    System.out.println("Total waktu: " + csp.countAlgorithmTime() + " detik");
                    csp.waktuProgramStop = System.currentTimeMillis();
                    System.out.println("Total waktu yang dihabiskan program: " + csp.countOverallTime() + " detik");
                    break;
                }
                case 2: {
                    System.out.println("");
                    System.out.println("Format otomatis (via file)");
                    System.out.println("");
                    System.out.println("File input yang tersedia:");
                    System.out.println("1. datainput.txt (satu soal)");
                    System.out.println("2. datainputcsplib.txt (sepuluh soal)");
                    System.out.println("");
                    System.out.print("Pilih file input: ");
                    File fileInput = null;
                    int pilihanFile = scan.nextInt();
                    switch (pilihanFile) {
                        case 1:
                            fileInput = new File("datainput.txt");
                            break;
                        case 2:
                            fileInput = new File("datainputcsplib.txt");
                            break;
                        default:
                            System.out.println("HANYA INPUT 1 ATAU 2");
                            System.exit(0);
                    }
                    CSP csp = new CSP();
                    csp.waktuProgramStart = System.currentTimeMillis();
                    String[] baris;
                    String line = "";
                    String separator = " ";
                    int programCounter = 0;
                    BufferedReader br;
                    BufferedWriter bw;
                    File hasilprogram = new File("hasilprogram.txt");
                    try {
                        System.out.println("Membaca file " + fileInput.getName() + "...");
                        br = new BufferedReader(new FileReader(fileInput));
                        bw = new BufferedWriter(new FileWriter(hasilprogram));
                        System.out.println("");

                        do {
                            programCounter++;
                            System.out.println("Soal ke-" + programCounter);
                            line = br.readLine();
                            baris = line.split(separator);
                            csp.totalMobil = Integer.parseInt(baris[0]);
                            System.out.println("Total mobil yang diproduksi: " + csp.totalMobil);
                            if (csp.totalMobil <= 0) {
                                System.out.println("TIDAK ADA MOBIL YANG DIURUTKAN");
                                System.exit(0);
                            }
                            csp.jmlOption = Integer.parseInt(baris[1]);
                            System.out.println("Jumlah option: " + csp.jmlOption);
                            if (csp.jmlOption <= 0) {
                                System.out.println("JUMLAH OPTION TIDAK VALID");
                                System.exit(0);
                            }
                            csp.jmlTipe = Integer.parseInt(baris[2]);
                            System.out.println("Jumlah tipe mobil: " + csp.jmlTipe);
                            if (csp.jmlTipe <= 0) {
                                System.out.println("JUMLAH TIPE TIDAK VALID");
                                System.exit(0);
                            }

                            System.out.println("");

                            csp.mobilKerja = new int[csp.jmlOption];
                            csp.mobilMasuk = new int[csp.jmlOption];

                            line = br.readLine();
                            baris = line.split(separator);
                            //Capacity Constraint: mobilKerja : mobilMasuk
                            for (int i = 0; i < csp.mobilKerja.length; i++) {
                                if (i >= baris.length) {
                                    System.out.println("JUMLAH WORKSTATION TIDAK SESUAI DENGAN JUMLAH OPTION");
                                    System.exit(0);
                                }
                                csp.mobilKerja[i] = Integer.parseInt(baris[i]);
                                if (csp.mobilKerja[i] == 0) {
                                    System.out.println("CAPACITY CONSTRAINT TIDAK VALID (MOBIL YANG DIKERJAKAN = 0)");
                                    System.exit(0);
                                }
                                System.out.println("Mobil yang dapat dikerjakan di workstation " + (i + 1) + ": " + csp.mobilKerja[i]);
                            }
                            System.out.println("");
                            line = br.readLine();
                            baris = line.split(separator);
                            for (int i = 0; i < csp.mobilMasuk.length; i++) {
                                if (i >= baris.length) {
                                    System.out.println("JUMLAH WORKSTATION TIDAK SESUAI DENGAN JUMLAH OPTION");
                                    System.exit(0);
                                }
                                csp.mobilMasuk[i] = Integer.parseInt(baris[i]);
                                if (csp.mobilMasuk[i] == 0) {
                                    System.out.println("CAPACITY CONSTRAINT TIDAK VALID (MOBIL YANG MASUK = 0)");
                                    System.exit(0);
                                }
                                System.out.println("Mobil yang akan masuk ke workstation " + (i + 1) + ": " + csp.mobilMasuk[i]);
                            }
                            System.out.println("");

                            //karakter untuk tipe mobil
                            csp.tipeMobil = new String[csp.jmlTipe];
                            int a = 0;
                            int b = 0;
                            int c = 0;
                            for (int i = 0; i < csp.jmlTipe; i++) {
                                if (a > 25) {
                                    csp.tipeMobil[i] = String.valueOf((char) (65 + b)) + String.valueOf((char) (65 + c));
                                    c++;
                                    if (a > 50) {
                                        b++;
                                    }
                                } else {
                                    csp.tipeMobil[i] = String.valueOf((char) (65 + a));
                                    a++;
                                }
                            }

                            //untuk tiap tipe, masukkan mobil yang akan diproduksi dan kebutuhan optionnya
                            csp.produksiPerTipe = new int[csp.jmlTipe];
                            csp.matriksTipeOption = new int[csp.jmlTipe][csp.jmlOption];
                            csp.tempTotalMobil = csp.totalMobil;
                            for (int i = 0; i < csp.jmlTipe; i++) {
                                line = br.readLine();
                                baris = line.split(separator);
                                System.out.println("Untuk mobil tipe " + csp.tipeMobil[Integer.parseInt(baris[0])] + ": ");
                                csp.prod = Integer.parseInt(baris[1]);
                                System.out.println("    Banyaknya mobil yang diproduksi: " + csp.prod);
                                csp.tempTotalMobil -= csp.prod;
                                if (csp.tempTotalMobil < 0) {
                                    System.out.println("JUMLAH MOBIL PER TIPE MELEBIHI TOTAL MOBIL YANG DIPRODUKSI");
                                    System.exit(0);
                                } else if (csp.prod == 0) {
                                    System.out.println("TERDAPAT TIPE MOBIL YANG TIDAK DIPRODUKSI");
                                    System.exit(0);
                                }
                                csp.produksiPerTipe[i] = csp.prod;
                                System.out.print("    Urutan matriks berdasarkan optionnya: ");
                                for (int j = 0; j < csp.jmlOption; j++) {
                                    csp.matriksTipeOption[i][j] = Integer.parseInt(baris[j + 2]);
                                    System.out.print(csp.matriksTipeOption[i][j] + " ");
                                }
                                System.out.println("");
                            }
                            System.out.println("");

                            try {
                                System.out.println("Program sedang menjalankan: ");
                                System.out.println("--Perhitungan Dasar CSP--");
                                CSPCounter cspCount = new CSPCounter(csp.jmlOption);
                                String avgUtil = cspCount.countCSP(csp.produksiPerTipe, csp.matriksTipeOption, csp.totalMobil, csp.mobilKerja, csp.mobilMasuk);
                                if (Double.parseDouble(avgUtil) == 0) {
                                    System.out.println("SEMUA OPTION TIDAK TERPAKAI");
                                    System.exit(0);
                                }
                                bw.write("Soal ke-" + programCounter);
                                bw.newLine();
                                bw.write("CAR SEQUENCING PROBLEM");
                                bw.newLine();
                                bw.write("Berdasarkan perhitungan CSP, average utility dari soal tersebut adalah " + avgUtil);
                                bw.newLine();

                                Populasi pop = new Populasi();
                                pop.inisialisasiPopulasi(50, csp.totalMobil, csp.produksiPerTipe);
                                Kromosom[] initPop = pop.kromosom;

                                System.out.println("--Genetic Algorithm--");
                                //Genetic Algorithm
                                csp.gaStart = System.currentTimeMillis();

                                GeneticAlgorithm ga = new GeneticAlgorithm();
                                ga.getSolution(initPop, csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);

                                csp.gaStop = System.currentTimeMillis();

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
                                csp.saStart = System.currentTimeMillis();

                                SimulatedAnnealing sa = new SimulatedAnnealing();
                                sa.inisialisasiVariabel(initPop);
                                sa.getSolution(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);

                                csp.saStop = System.currentTimeMillis();

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
                                csp.gasaStart = System.currentTimeMillis();

                                HybridGASA gasa = new HybridGASA(initPop, 25);
                                gasa.solutionGASA(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);

                                csp.gasaStop = System.currentTimeMillis();

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
                                csp.sagaStart = System.currentTimeMillis();

                                HybridSAGA saga = new HybridSAGA(initPop, 25);
                                saga.solutionSAGA(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);

                                csp.sagaStop = System.currentTimeMillis();

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
                                csp.cyclicStart = System.currentTimeMillis();

                                CyclicGASA cyclic = new CyclicGASA(initPop, 25, 10);
                                cyclic.solutionCyclic(csp.mobilKerja, csp.mobilMasuk, csp.matriksTipeOption);

                                csp.cyclicStop = System.currentTimeMillis();

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
                                System.out.println("Genetic Algorithm: " + csp.countGATime() + " detik");
                                System.out.println("Simulated Annealing: " + csp.countSATime() + " detik");
                                System.out.println("Hybrid GA-SA: " + csp.countGASATime() + " detik");
                                System.out.println("Hybrid SA-GA: " + csp.countSAGATime() + " detik");
                                System.out.println("Cyclic GA-SA: " + csp.countCyclicTime() + " detik");
                                System.out.println("");
                                System.out.println("Total waktu: " + csp.countAlgorithmTime() + " detik");

//                    bw.close();
                                System.out.println("");
                                System.out.println("File hasil (hasilprogram.txt) sudah tertulis.");

                                System.out.println("");
                                bw.newLine();
                            } catch (IOException ex) {
                                System.err.println("FILE TIDAK DAPAT TERTULIS");
                            }
                        } while ((line = br.readLine()) != null);
                        line = br.readLine();
                        bw.close();

                        if (!Desktop.isDesktopSupported()) {
                            System.out.println("not supported");
                            return;
                        }
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(hasilprogram);
                    } catch (IOException ex) {
                        System.out.println("FILE TIDAK DITEMUKAN");
                    } catch (NumberFormatException num) {
                        System.out.println("TERDAPAT INPUT NON ANGKA");
                    } catch (NullPointerException non) {
                        System.out.println("INPUT TIDAK LENGKAP");
                    }
                    csp.waktuProgramStop = System.currentTimeMillis();
                    System.out.println("Total waktu yang dihabiskan program: " + csp.countOverallTime() + " detik");
                    break;
                }
                default:
                    System.out.println("HANYA INPUT 1 ATAU 2");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("HANYA MENERIMA MASUKAN ANGKA");
            System.exit(0);
        }

    }

}

class CSP {

    public int totalMobil, jmlOption, jmlTipe, tempTotalMobil, prod;
    public int[] mobilKerja, mobilMasuk, produksiPerTipe;
    public int[][] matriksTipeOption;
    public String[] tipeMobil;
    public double waktuProgramStart, waktuProgramStop, gaStart, gaStop,
            saStart, saStop, gasaStart, gasaStop, sagaStart, sagaStop, cyclicStart, cyclicStop;

    public double countGATime() {
        return (this.gaStop - this.gaStart) / 1000;
    }

    public double countSATime() {
        return (this.saStop - this.saStart) / 1000;
    }

    public double countGASATime() {
        return (this.gasaStop - this.gasaStart) / 1000;
    }

    public double countSAGATime() {
        return (this.sagaStop - this.sagaStart) / 1000;
    }

    public double countCyclicTime() {
        return (this.cyclicStop - this.cyclicStart) / 1000;
    }

    public double countAlgorithmTime() {
        return (this.cyclicStop - this.gaStart) / 1000;
    }

    public double countOverallTime() {
        return (this.waktuProgramStop - this.waktuProgramStart) / 1000;
    }
}
