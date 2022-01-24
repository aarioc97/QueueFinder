/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elementary_components;

import java.util.Random;

/**
 *
 * @author i15055
 */
public class Kromosom {
    //variabel tipeMobil dibuat karena tidak mengambil langsung
    //jmlTipe dari kelas QueueFinder

    public int gagalDikerjakan, delay;
    public int[] kromosom;
    //dimensi pertama: urutan, dimensi kedua: matriks tipe option
    public int[][] urutan;
    private int[] tempMasuk;
    private int[] idTipe;

    public Kromosom(int total, int[] prodPerTipe) {
        this.kromosom = new int[total];
//        this.tipeMobil = new int[prodPerTipe.length];
        this.idTipe = new int[total];
//        for (int i = 0; i < this.tipeMobil.length; i++) {
//            this.tipeMobil[i] = i;
//        }

        int counter = 0;
        for (int i = 0; i < prodPerTipe.length; i++) {
            for (int j = 0; j < prodPerTipe[i]; j++) {
//                this.kromosom[counter] = this.tipeMobil[i];
                this.kromosom[counter] = counter;
                this.idTipe[counter] = i;
                counter++;
            }
        }

        //Fisher - Yates Shuffle Array Function
        Random rn = new Random();
        for (int i = this.kromosom.length - 1; i > 0; i--) {
            int index = rn.nextInt(i + 1);
            int a = this.kromosom[index];
            this.kromosom[index] = this.kromosom[i];
            this.kromosom[i] = a;
        }
    }

    //cari urutan mobil yang dapat / tidak dapat dikerjakan
    public void hitungFitnessKromosom(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        this.gagalDikerjakan = 0;
        this.delay = 0;

        this.urutan = new int[this.kromosom.length][matriksTipeOption[0].length];

        for (int i = 0; i < this.urutan.length; i++) {
            for (int j = 0; j < matriksTipeOption[j].length; j++) {
                this.urutan[i][j] = matriksTipeOption[this.idTipe[this.kromosom[i]]][j];
            }
        }

        this.tempMasuk = new int[mobilMasuk.length];
        for (int i = 0; i < this.tempMasuk.length; i++) {
            this.tempMasuk[i] = mobilMasuk[i];
        }

        int[] urutanTerakhir = new int[this.tempMasuk.length];
        int[] jmlKerja = new int[this.tempMasuk.length];
        boolean[] trigger = new boolean[this.tempMasuk.length];
        for (int i = 0; i < this.urutan.length; i++) {
            for (int j = 0; j < this.tempMasuk.length; j++) {
                if (this.urutan[i][j] == 1) {
                    trigger[j] = true;
                    jmlKerja[j]++;
                }
                if (trigger[j] == true) {
                    this.tempMasuk[j]--;
                }
                if (this.tempMasuk[j] == 0 || i == this.urutan.length - 1) {
                    trigger[j] = false;
                    this.tempMasuk[j] = mobilMasuk[j];
                    if (urutanTerakhir[j] == 1) {
                        jmlKerja[j]++;
                    }
                    urutanTerakhir[j] = this.urutan[i][j];
                    if (j > 0) {
                        if (this.urutan[i][j - 1] == -1) {
                            jmlKerja[j] = 0;
                            break;
                        }
                    }
                    if (jmlKerja[j] > mobilKerja[j]) {
                        this.gagalDikerjakan += jmlKerja[j] - mobilKerja[j];
                        this.delay += mobilMasuk[j] * (jmlKerja[j] - mobilKerja[j]);
                        this.urutan[i][j] = -1;
                    }
                    jmlKerja[j] = 0;
                } else {
                    if (this.urutan[i][j] == 0) {
                        urutanTerakhir[j] = 0;
                    }
                }
            }
        }
    }
}
