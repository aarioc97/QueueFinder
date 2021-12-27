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
public class SimulatedAnnealing {

    public Populasi populasi;
    public Kromosom initialSolution, neighborSolution;
    private final Random rn = new Random();
    private int initialTemp, selisihTemp, repetitionSch, selisihFitness;

    public void inisialisasiVariabel(Kromosom[] initPop) {

        this.populasi = new Populasi();
        this.populasi.kromosom = initPop;

        //Tentukan solusi awal
        this.initialSolution = this.populasi.kromosom[0];

        //Tentukan jadwal penurunan temperatur
        //temperatur akan turun sesuai dengan selisih yang ditentukan
        //selisih penurunan temperatur adalah nilai random
        //untuk kasus ini, maksimal selisih adalah 10, minimalnya 1
        this.selisihTemp = 30;

        //Tentukan temperatur awal
        //temperatur awal adalah banyaknya kromosom di populasi
        this.initialTemp = this.populasi.kromosom.length;

        //Tentukan jadwal repetisi
        //yaitu banyaknya iterasi yang dilakukan untuk setiap penurunan temperatur
        //nilai jadwal repetisi ditentukan secara random
        //minimal 1 kali, maksimal banyaknya kromosom di populasi dikurangi 1
        this.repetitionSch = this.rn.nextInt(this.populasi.kromosom.length - 1) + 1;

    }

    private void cariDeltaFitness(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        this.initialSolution.hitungFitnessKromosom(mobilKerja, mobilMasuk, matriksTipeOption);
        this.neighborSolution.hitungFitnessKromosom(mobilKerja, mobilMasuk, matriksTipeOption);
        this.selisihFitness = this.neighborSolution.gagalDikerjakan - this.initialSolution.gagalDikerjakan;
    }

    private double hitungKemungkinan() {
        return Math.exp(Math.negateExact(this.selisihFitness) / this.selisihTemp);
    }

    private void tentukanSolusiAwal() {
        if (this.selisihFitness <= 0) {
            this.initialSolution = this.neighborSolution;
        }
        if (this.selisihFitness > 0) {
            if (this.hitungKemungkinan() == 1.0) {
                this.initialSolution = this.neighborSolution;
            }
        }
    }

    public void getSolution(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {

        //Temperatur akan diturunkan hingga mencapai 0
        for (int i = this.initialTemp; i > 0; i = -this.selisihTemp) {
            //Untuk setiap penurunan temperatur, akan terjadi pengulangan
            //sebanyak jadwal repetisi
            for (int j = this.repetitionSch; j > 0; j--) {
                //Tetangga dari solusi awal adalah kromosom lain dalam populasi.
                //Solusi tetangga dipilih secara random.
                int kromNeighbor = this.rn.nextInt(this.populasi.kromosom.length - 1);
                this.neighborSolution = this.populasi.kromosom[kromNeighbor];
                //Hitung fitness dari masing-masing kromosom
                //lalu tentukan deltanya
                this.cariDeltaFitness(mobilKerja, mobilMasuk, matriksTipeOption);
                //Tentukan apakah solusi awal diganti dengan neighbor atau tidak
                this.tentukanSolusiAwal();
            }
        }

    }

}
