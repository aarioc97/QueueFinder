/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elementary_algorithms;

import java.util.Random;
import elementary_components.Kromosom;
import elementary_components.Populasi;

/**
 *
 * @author i15055
 */
public class GeneticAlgorithm {

    public Populasi populasi = new Populasi();
    public Kromosom kromTerbaik, kromTerbaikKedua;
    private double crossoverRate, mutationRate;
    private int generationCount = 0;

    public void selection(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        this.populasi.hitungFitness(mobilKerja, mobilMasuk, matriksTipeOption);
        this.kromTerbaik = this.populasi.kromTerbaik;
        this.kromTerbaikKedua = this.populasi.kromTerbaikKedua;
    }

    public void crossover() {
        Random rn = new Random();

        //Cari crossover point secara random
        int crossOverPoint = rn.nextInt(populasi.kromTerbaik.kromosom.length);

        //Modified Partially Mapped Crossover
        for (int i = 0; i < this.kromTerbaik.kromosom.length; i++) {
            boolean clear = false;
            if (i < crossOverPoint) {
                int temp = this.kromTerbaik.kromosom[i];
                this.kromTerbaik.kromosom[i] = this.kromTerbaikKedua.kromosom[i];
                this.kromTerbaikKedua.kromosom[i] = temp;
            } else {
                while (clear == false) {
                    clear = true;
                    for (int j = 0; j < crossOverPoint; j++) {
                        if (this.kromTerbaik.kromosom[i] == this.kromTerbaik.kromosom[j]) {
                            this.kromTerbaik.kromosom[i] = this.kromTerbaikKedua.kromosom[j];
                            clear = false;
                            break;
                        }
                        if (this.kromTerbaikKedua.kromosom[i] == this.kromTerbaikKedua.kromosom[j]) {
                            this.kromTerbaikKedua.kromosom[i] = this.kromTerbaik.kromosom[j];
                            clear = false;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void mutation() {
        Random rn = new Random();

        //Pilih anak yang akan dilakukan mutasi
        int pilihAnak = rn.nextInt(2);

        //Cari mutation point secara random untuk kromosom terbaik
        int mutationPoint = rn.nextInt(populasi.kromTerbaik.kromosom.length);
        int counter = mutationPoint;

        if (pilihAnak == 0) {
            //Putar balik urutan gen
            //dari gen awal kromosom hingga gen di mutation point
            for (int i = 0; i < mutationPoint / 2 + 1; i++) {
                int temp = this.kromTerbaik.kromosom[i];
                this.kromTerbaik.kromosom[i] = this.kromTerbaik.kromosom[counter];
                this.kromTerbaik.kromosom[counter] = temp;
                counter--;
            }
        } else if (pilihAnak == 1) {
            //Putar balik urutan gen
            //dari gen awal kromosom hingga gen di mutation point
            for (int i = 0; i < mutationPoint / 2 + 1; i++) {
                int temp = this.kromTerbaikKedua.kromosom[i];
                this.kromTerbaikKedua.kromosom[i] = this.kromTerbaikKedua.kromosom[counter];
                this.kromTerbaikKedua.kromosom[counter] = temp;
                counter--;
            }
        }
    }

    //Memasukkan anak terbaik ke dalam populasi
    //untuk menggantikan kromosom terburuk
    public void masukkanAnakTerbaik(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {

        //Perbarui kegagalan tiap kromosom di atas
        //setelah dilakukan selection, crossover, mutation
        this.kromTerbaik.hitungFitnessKromosom(mobilKerja, mobilMasuk, matriksTipeOption);
        this.kromTerbaikKedua.hitungFitnessKromosom(mobilKerja, mobilMasuk, matriksTipeOption);

        //Mencari index kromosom terburuk dalam populasi
        int indexKromosomTerburuk = this.populasi.cariIndexKromosomTerburuk();

        //Ganti kromosom terburuk dengan anak terbaik
        //dari dua anak di atas
        if (this.kromTerbaik.gagalDikerjakan > this.kromTerbaikKedua.gagalDikerjakan) {
            this.populasi.kromosom[indexKromosomTerburuk] = this.kromTerbaik;
        } else {
            this.populasi.kromosom[indexKromosomTerburuk] = this.kromTerbaikKedua;
        }
    }

    public void getSolution(Kromosom[] initPop, int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption, double crossRate, double mutateRate) {
        this.populasi.kromosom = initPop;
        this.populasi.hitungFitness(mobilKerja, mobilMasuk, matriksTipeOption);

        //crossoverRate dan mutationRate dalam satuan persen
        //Persentase menunjukkan berapa kali crossover dan mutation dilakukan
        //Berapa kali dilakukan = banyaknya kromosom dalam populasi yang mengalaminya
        this.crossoverRate = crossRate;
        this.mutationRate = mutateRate;
        int crossoverCounter = 0;
        int mutationCounter = 0;

        int counter = 0;
        while (this.populasi.jmlGagalKromosom > 0 && counter < 1000) {

            //Jika memenuhi rate, crossover atau mutation dilakukan
            //Counter untuk keduanya untuk menghitung berapa kali dilakukan
            this.generationCount++;
            this.populasi.cariDuaKromosomTerbaik();
            this.selection(mobilKerja, mobilMasuk, matriksTipeOption);

            double maxCrossover = (crossoverRate / 100) * (double) this.populasi.kromosom.length;
            double maxMutation = (mutationRate / 100) * (double) this.populasi.kromosom.length;
            
            if (crossoverCounter < maxCrossover) {
                this.crossover();
                crossoverCounter++;
            }
            if (mutationCounter < maxMutation) {
                this.mutation();
                mutationCounter++;
            }

            //Masukkan kedua anak terbaik ke dalam populasi
            //lalu hitung fitness populasi
            this.masukkanAnakTerbaik(mobilKerja, mobilMasuk, matriksTipeOption);
            this.populasi.hitungFitness(mobilKerja, mobilMasuk, matriksTipeOption);

            //Counter untuk membatasi iterasi
            counter++;
        }
    }
}
