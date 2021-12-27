/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elementary_components;

/**
 *
 * @author i15055
 */
public class Populasi {

    public int jmlGagalKromosom = 0;
    public Kromosom kromTerbaik, kromTerbaikKedua;
    public Kromosom[] kromosom;

    public void inisialisasiPopulasi(int banyakKromosom, int total, int[] prodPerTipe) {
        this.kromosom = new Kromosom[banyakKromosom];
        for (int i = 0; i < this.kromosom.length; i++) {
            this.kromosom[i] = new Kromosom(total, prodPerTipe);
        }
    }

    public void cariDuaKromosomTerbaik() {
        int terbaikIndex = this.kromosom.length - 1;
        int terbaikKeduaIndex = this.kromosom.length - 1;
        for (int i = 0; i < this.kromosom.length; i++) {
            if (this.kromosom[i].gagalDikerjakan < this.kromosom[terbaikIndex].gagalDikerjakan) {
                terbaikKeduaIndex = terbaikIndex;
                terbaikIndex = i;
            } else if (this.kromosom[i].gagalDikerjakan < this.kromosom[terbaikKeduaIndex].gagalDikerjakan) {
                terbaikKeduaIndex = i;
            }
        }
        this.jmlGagalKromosom = this.kromosom[terbaikIndex].gagalDikerjakan;
        this.kromTerbaik = this.kromosom[terbaikIndex];
        this.kromTerbaikKedua = this.kromosom[terbaikKeduaIndex];
    }

    public int cariIndexKromosomTerburuk() {
        int gagalTerburuk = Integer.MAX_VALUE;
        int indexTerburuk = 0;
        for (int i = 0; i < this.kromosom.length; i++) {
            if (gagalTerburuk >= this.kromosom[i].gagalDikerjakan) {
                gagalTerburuk = this.kromosom[i].gagalDikerjakan;
                indexTerburuk = i;
            }
        }
        return indexTerburuk;
    }

    public void hitungFitness(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {

        for (Kromosom kromosom1 : this.kromosom) {
            kromosom1.hitungFitnessKromosom(mobilKerja, mobilMasuk, matriksTipeOption);
        }
        cariDuaKromosomTerbaik();
    }
}
