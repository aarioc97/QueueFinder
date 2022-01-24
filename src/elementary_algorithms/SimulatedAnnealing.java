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
    //Variabel repeat untuk menghitung berapa kali penurunan suhu
    //Variabel vcf merepresentasikan Variable Cooling Factor
    private int initialTemp, /*selisihTemp,*/ repetitionSch, selisihFitness, repeat;
    private double vcf, currentTemp, acceptanceProbability;

    public void inisialisasiVariabel(Kromosom[] initPop, double acceptanceProb) {

        this.populasi = new Populasi();
        this.populasi.kromosom = initPop;

        //Tentukan solusi awal
        this.initialSolution = this.populasi.kromosom[0];

        //Tentukan temperatur awal
        //temperatur awal adalah banyaknya kromosom di populasi
        this.initialTemp = this.populasi.kromosom.length;

        //Inisialisasi variabel yang bersangkutan dengan penurunan suhu
        //temperatur akan turun sesuai dengan perhitungan VCF
        //semakin rendah suhu, probabilitas penerimaan solusi semakin kecil
        this.currentTemp = this.initialTemp;
        this.repeat = 0;
        this.vcf = 0;

        //Tentukan jadwal repetisi
        //yaitu banyaknya iterasi yang dilakukan untuk setiap penurunan temperatur
        //nilai jadwal repetisi ditentukan secara random
        //minimal 1 kali, maksimal banyaknya kromosom di populasi dikurangi 1
        this.repetitionSch = this.rn.nextInt(this.populasi.kromosom.length - 1) + 1;
        
        //Inisialisasi variabel batas penerimaan solusi buruk
        //Variabel ditentukan oleh parameter
        //Jika perhitungan kemungkinan di bawah variabel ini, solusi buruk tidak akan diterima
        //Dalam bentuk desimal dengan rentang 0.0 (selalu diterima) hingga 1.0 (tidak menerima)
        //Nilai default di 0.8 (penerimaan sekitar 5/6 solusi buruk)
        this.acceptanceProbability = acceptanceProb;

    }

    private void cariDeltaFitness(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        this.initialSolution.hitungFitnessKromosom(mobilKerja, mobilMasuk, matriksTipeOption);
        this.neighborSolution.hitungFitnessKromosom(mobilKerja, mobilMasuk, matriksTipeOption);
        this.selisihFitness = this.neighborSolution.gagalDikerjakan - this.initialSolution.gagalDikerjakan;
    }

    private double hitungKemungkinan() {
        return Math.exp(Math.negateExact(this.selisihFitness) / this.currentTemp /*this.selisihTemp*/);
    }

    private void tentukanSolusiAwal() {
        if (this.selisihFitness <= 0) {
            this.initialSolution = this.neighborSolution;
        }
        if (this.selisihFitness > 0) {
            //Penggunaan variabel acceptanceProbability
            //Untuk membatasi penerimaan solusi buruk
            if (this.hitungKemungkinan() >= this.acceptanceProbability) {
                //Usahakan penerimaan solusi buruk hanya pada suhu tinggi
                //semakin rendah suhu, semakin kecil probabilitasnya
                //angka 0.8 dipilih setelah tes, rentang nilai probabilitas 0.3-0.9
                this.initialSolution = this.neighborSolution;
            }
        }
    }

    public void getSolution(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {

        //Temperatur akan diturunkan hingga mencapai 0
        while (currentTemp > 1) {

            //Perhitungan VCF dan penentuan current temperature
            this.vcf = Math.pow(1 + (1 / Math.sqrt((repeat * this.repetitionSch) + (this.repetitionSch - 1))), -1);
            this.currentTemp *= vcf;
            this.repeat++;

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
//            System.out.println(String.format("%.2f", this.currentTemp) + " " + String.format("%.2f", this.vcf));
        }

//        for (int i = this.initialTemp; i > 0; i = -this.selisihTemp) {
//            
//        }
    }

}
