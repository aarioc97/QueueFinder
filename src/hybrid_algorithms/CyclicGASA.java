/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hybrid_algorithms;

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
    private double crossoverRate, mutationRate, acceptanceProbability;
    private Kromosom[] kromTerbaikTerakhir, kromTerbaikSA;
    private final int timeout, timeoutCyclic;

    public CyclicGASA(Kromosom[] initialPop, int timeout, int timeoutCyclic, double crossRate, double mutateRate, double accProb) {
        this.initialPop = initialPop;
        this.timeout = timeout;
        this.timeoutCyclic = timeoutCyclic;
        this.crossoverRate = crossRate;
        this.mutationRate = mutateRate;
        this.acceptanceProbability = accProb;
    }

    public void solutionCyclic(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout / 10;
        for (int i = 0; i < this.timeoutCyclic; i++) {
            if (i == 0 || i % 2 == 0 && this.kromTerbaikTerakhir.length > 2) {
                this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];

                for (int j = 0; j < this.timeout; j++) {
                    //Nilai Default:
                    //Crossover Rate: 50%
                    //Mutation Rate: 5%
                    this.ga = new GeneticAlgorithm();
                    this.ga.getSolution(initialPop, mobilKerja, mobilMasuk, matriksTipeOption, this.crossoverRate, this.mutationRate);

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
                    sa.inisialisasiVariabel(this.kromTerbaikTerakhir, this.acceptanceProbability);
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
        for (Kromosom kromTerbaikSA1 : this.kromTerbaikSA) {
            if (kromTerbaikSA1.gagalDikerjakan < hasil.gagalDikerjakan) {
                hasil = kromTerbaikSA1;
            } else if (kromTerbaikSA1.gagalDikerjakan == 0) {
                break;
            }
        }
        return hasil;
    }

}
