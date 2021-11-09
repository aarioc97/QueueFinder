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
public class CyclicGASA {

    public GeneticAlgorithm ga;
    public SimulatedAnnealing sa;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    public Kromosom[] kromTerbaikTerakhir, kromTerbaikSA;
    private int timeout, timeoutCyclic;

    public CyclicGASA(Kromosom[] initialPop, int timeout, int timeoutCyclic) {
        this.initialPop = initialPop;
        this.timeout = timeout;
        this.timeoutCyclic = timeoutCyclic;
    }

    public void solutionCyclic(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout / 10;
        for (int i = 0; i < this.timeoutCyclic; i++) {
            if (i == 0 || i % 2 == 0 && this.kromTerbaikTerakhir.length > 2) {
                this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];

                for (int j = 0; j < this.timeout; j++) {
                    this.ga = new GeneticAlgorithm();
                    this.ga.getSolution(initialPop, mobilKerja, mobilMasuk, matriksTipeOption);

                    if (j >= this.timeout - jmlKromDiambil) {
                        this.kromTerbaikTerakhir[j - (this.timeout - jmlKromDiambil)] = this.ga.populasi.kromTerbaik;
                    }
                }
                this.hasilTerbaik = this.ga.populasi.kromTerbaik;
                this.initialPop = this.kromTerbaikTerakhir;
            } else if (this.kromTerbaikTerakhir.length > 2) {
                this.kromTerbaikSA = new Kromosom[this.timeout];
                for (int j = 0; j < this.timeout; j++) {
                    this.sa = new SimulatedAnnealing();
                    sa.inisialisasiVariabel(this.kromTerbaikTerakhir);
                    sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);
                    this.kromTerbaikSA[j] = this.sa.initialSolution;

                    if (j < this.kromTerbaikTerakhir.length) {
                        this.kromTerbaikTerakhir[j] = this.sa.initialSolution;
                    }
                }
                this.hasilTerbaik = this.cariKromosomHasilTerbaik();
                this.initialPop = this.kromTerbaikTerakhir;
            }
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
