/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hybrid_algorithms;

import java.util.Random;
import elementary_algorithms.GeneticAlgorithm;
import elementary_components.Kromosom;
import elementary_algorithms.SimulatedAnnealing;

/**
 *
 * @author i15055
 */
public class HybridGASA {

    //50% solusi terbaik terakhir dari GA dimasukkan ke SA
    public GeneticAlgorithm ga;
    public SimulatedAnnealing sa;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    private Kromosom[] kromTerbaikTerakhir, kromTerbaikSA;
    private int timeout;

    public HybridGASA(Kromosom[] initialPop, int timeout) {
        this.initialPop = initialPop;
        this.timeout = timeout;
    }

    public void solutionGASA(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout / 2;
        this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];

        for (int i = 0; i < this.timeout; i++) {
            this.ga = new GeneticAlgorithm();
            this.ga.getSolution(initialPop, mobilKerja, mobilMasuk, matriksTipeOption);

            if (i >= this.timeout - jmlKromDiambil) {
                this.kromTerbaikTerakhir[i - (this.timeout - jmlKromDiambil)] = this.ga.populasi.kromTerbaik;
            }
        }
        this.hasilTerbaik = this.ga.populasi.kromTerbaik;

        if (this.kromTerbaikTerakhir.length > 2) {
            this.kromTerbaikSA = new Kromosom[this.timeout];
            for (int i = 0; i < this.timeout; i++) {
                this.sa = new SimulatedAnnealing();
                this.sa.inisialisasiVariabel(this.kromTerbaikTerakhir);
                this.sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);
                this.kromTerbaikSA[i] = this.sa.initialSolution;
            }
            this.hasilTerbaik = this.cariKromosomHasilTerbaik();
        }

    }

    private Kromosom cariKromosomHasilTerbaik() {
        Kromosom hasil = this.kromTerbaikSA[0];
        for (int i = 0; i < this.kromTerbaikSA.length; i++) {
            if (this.kromTerbaikSA[i].gagalDikerjakan < hasil.gagalDikerjakan) {
                hasil = this.kromTerbaikSA[i];
            }
        }
        return hasil;
    }

}
