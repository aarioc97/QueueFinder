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
public class HybridSAGA {

    public SimulatedAnnealing sa;
    public GeneticAlgorithm ga;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    private Kromosom[] kromTerbaikTerakhir, kromTerbaikSA;
    private int timeout;

    public HybridSAGA(Kromosom[] initialPop, int timeout) {
        this.initialPop = initialPop;
        this.timeout = timeout;
    }

    public void solutionSAGA(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout * 4 / 5;
        this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];
        this.kromTerbaikSA = new Kromosom[this.timeout];

        for (int i = 0; i < this.timeout; i++) {
            this.sa = new SimulatedAnnealing();
            this.sa.inisialisasiVariabel(this.initialPop);
            this.sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);
            this.kromTerbaikSA[i] = this.sa.initialSolution;

            if (i >= this.timeout - jmlKromDiambil) {
                this.kromTerbaikTerakhir[i - (this.timeout - jmlKromDiambil)] = this.sa.initialSolution;
            }
        }
        this.hasilTerbaik = this.cariKromosomHasilTerbaik();

        if (this.kromTerbaikTerakhir.length > 2) {
            for (int i = 0; i < this.timeout; i++) {
                this.ga = new GeneticAlgorithm();
                this.ga.getSolution(this.kromTerbaikTerakhir, mobilKerja, mobilMasuk, matriksTipeOption);
            }
            this.hasilTerbaik = this.ga.populasi.kromTerbaik;
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
