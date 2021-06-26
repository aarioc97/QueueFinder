/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gasa_component;

import java.util.Random;

/**
 *
 * @author i15055
 */
public class Kromosom {
//variabel tipeMobil dibuat karena tidak mengambil langsung
    //jmlTipe dari kelas QueueFinder

    public int gagalDikerjakan, delay;
    public int[] gen;
    public int[][] urutan;
    private int[] tipeMobil, tempMasuk;

    public Kromosom(int total, int[] prodPerTipe) {
        this.gen = new int[total] /*{4, 2, 3, 3, 3, 4, 2, 4, 2, 1, 1, 3}*/;
        this.tipeMobil = new int[prodPerTipe.length];
        for (int i = 0; i < this.tipeMobil.length; i++) {
            this.tipeMobil[i] = i;
        }

        int counter = 0;
        for (int i = 0; i < this.tipeMobil.length; i++) {
            for (int j = 0; j < prodPerTipe[i]; j++) {
                this.gen[counter] = this.tipeMobil[i];
                counter++;
            }
        }

        //Fisher - Yates Shuffle Array Function
        Random rn = new Random();
        for (int i = this.gen.length - 1; i > 0; i--) {
            int index = rn.nextInt(i + 1);
            int a = this.gen[index];
            this.gen[index] = this.gen[i];
            this.gen[i] = a;
        }
    }

//cari urutan mobil yang dapat / tidak dapat dikerjakan
    public void hitungFitnessKromosom(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        this.gagalDikerjakan = 0;
        this.delay = 0;

        this.urutan = new int[this.gen.length][matriksTipeOption[0].length];

        for (int i = 0; i < this.urutan.length; i++) {
            for (int j = 0; j < matriksTipeOption[j].length; j++) {
                this.urutan[i][j] = matriksTipeOption[this.gen[i]/* - 1*/][j];
            }
        }

        this.tempMasuk = new int[mobilMasuk.length];
        for (int i = 0; i < this.tempMasuk.length; i++) {
            this.tempMasuk[i] = mobilMasuk[i];
        }

        int[] urutanTerakhir = new int[this.tempMasuk.length];
        int[] urutanSebelum = new int[this.tempMasuk.length];
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
                urutanSebelum[j] = this.urutan[i][j];
            }
        }
    }
}
