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
    private int generationCount = 0;

    public void selection(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
//        populasi.cariDuaKromosomTerbaik();
        this.populasi.hitungFitness(mobilKerja, mobilMasuk, matriksTipeOption);
        this.kromTerbaik = this.populasi.kromTerbaik;
        this.kromTerbaikKedua = this.populasi.kromTerbaikKedua;
    }

    public void crossover() {
        Random rn = new Random();

        //Cari crossover point secara random
        int crossOverPoint = rn.nextInt(populasi.kromTerbaik.gen.length);

        //Tukar semua gen dari kedua kromosom terbaik
        //dari gen awal kromosom hingga gen di crossover point
        for (int i = 0; i < crossOverPoint; i++) {
            int temp = this.kromTerbaik.gen[i];
            this.kromTerbaik.gen[i] = this.kromTerbaikKedua.gen[i];
            this.kromTerbaikKedua.gen[i] = temp;
        }
    }

    public void mutation() {
        Random rn = new Random();

        //Pilih anak yang akan dilakukan mutasi
        int pilihAnak = rn.nextInt(2);

        //Cari mutation point secara random untuk kromosom terbaik
        int mutationPoint = rn.nextInt(populasi.kromTerbaik.gen.length);
        int counter = mutationPoint;

        if (pilihAnak == 0) {
            //Putar balik urutan gen
            //dari gen awal kromosom hingga gen di mutation point
            for (int i = 0; i < mutationPoint / 2 + 1; i++) {
                int temp = this.kromTerbaik.gen[i];
                this.kromTerbaik.gen[i] = this.kromTerbaik.gen[counter];
                this.kromTerbaik.gen[counter] = temp;
                counter--;
            }
        } else if (pilihAnak == 1) {
            //Putar balik urutan gen
            //dari gen awal kromosom hingga gen di mutation point
            for (int i = 0; i < mutationPoint / 2 + 1; i++) {
                int temp = this.kromTerbaikKedua.gen[i];
                this.kromTerbaikKedua.gen[i] = this.kromTerbaikKedua.gen[counter];
                this.kromTerbaikKedua.gen[counter] = temp;
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

    public void getSolution(Kromosom[] initPop, int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        this.populasi.kromosom = initPop;
        this.populasi.hitungFitness(mobilKerja, mobilMasuk, matriksTipeOption);
//        System.out.println("Generasi ke-" + ga.generationCount + ", gagal dikerjakan: " + ga.populasi.jmlGagalKromosom);

        Random rnd = new Random();
        int crossoverRate = 0;
        int mutationRate = 0;

        int counter = 0;
        while (this.populasi.jmlGagalKromosom > 0 && counter < 1000) {

            crossoverRate = rnd.nextInt(99) + 1;
            mutationRate = rnd.nextInt(99) + 1;
            this.generationCount++;
            this.populasi.cariDuaKromosomTerbaik();
            this.selection(mobilKerja, mobilMasuk, matriksTipeOption);
//            System.out.println("Dipilih gen ke-" + ga.populasi.idxTerbaik + " dan ke-" + ga.populasi.idxKeduaTerbaik);
            if (crossoverRate <= 60) {
//                System.out.println("Crossover dijalankan");
                this.crossover();
            }
//            this.crossover();
            if (mutationRate <= 5) {
//                System.out.println("Mutation dijalankan");
                this.mutation();
            }

            this.masukkanAnakTerbaik(mobilKerja, mobilMasuk, matriksTipeOption);
            this.populasi.hitungFitness(mobilKerja, mobilMasuk, matriksTipeOption);
//            System.out.println("Generasi ke-" + ga.generationCount + ", gagal dikerjakan: " + ga.populasi.jmlGagalKromosom);

            counter++;
        }

//        System.out.println("\nUrutan terbaik ada di generasi ke-" + ga.generationCount);
//        System.out.println("Jumlah mobil yang gagal dikerjakan: " + ga.populasi.getKromosomTerbaik().gagalDikerjakan);
//        System.out.println("");
    }
}
