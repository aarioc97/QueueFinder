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
public class HybridSAGA {

    public SimulatedAnnealing sa;
    public GeneticAlgorithm ga;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    private double crossoverRate, mutationRate, acceptanceProbability;
    private Kromosom[] kromTerbaikTerakhir, kromTerbaikSA;
    private final int timeout;

    public HybridSAGA(Kromosom[] initialPop, int timeout, double crossRate, double mutateRate, double accProb) {
        this.initialPop = initialPop;
        this.timeout = timeout;
        this.crossoverRate = crossRate;
        this.mutationRate = mutateRate;
        this.acceptanceProbability = accProb;
    }

    public void solutionSAGA(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout * 4 / 5;
        this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];
        this.kromTerbaikSA = new Kromosom[this.timeout];

        for (int i = 0; i < this.timeout; i++) {
            this.sa = new SimulatedAnnealing();
            this.sa.inisialisasiVariabel(this.initialPop, this.acceptanceProbability);
            this.sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);
            this.kromTerbaikSA[i] = this.sa.initialSolution;

            if (i >= this.timeout - jmlKromDiambil) {
                this.kromTerbaikTerakhir[i - (this.timeout - jmlKromDiambil)] = this.sa.initialSolution;
            }
        }
        this.hasilTerbaik = this.cariKromosomHasilTerbaik();

        if (this.kromTerbaikTerakhir.length > 2) {
            for (int i = 0; i < this.timeout; i++) {
                //Nilai Default:
                //Crossover Rate: 50%
                //Mutation Rate: 5%
                this.ga = new GeneticAlgorithm();
                this.ga.getSolution(this.kromTerbaikTerakhir, mobilKerja, mobilMasuk, matriksTipeOption, this.crossoverRate, this.mutationRate);
            }
            this.hasilTerbaik = this.ga.populasi.kromTerbaik;
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
