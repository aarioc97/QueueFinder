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
public class HybridGASA {

    public GeneticAlgorithm ga;
    public SimulatedAnnealing sa;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    private double crossoverRate, mutationRate, acceptanceProbability;
    private Kromosom[] kromTerbaikTerakhir, kromTerbaikSA;
    private final int timeout;

    public HybridGASA(Kromosom[] initialPop, int timeout, double crossRate, double mutateRate, double accProb) {
        this.initialPop = initialPop;
        this.timeout = timeout;
        this.crossoverRate = crossRate;
        this.mutationRate = mutateRate;
        this.acceptanceProbability = accProb;
    }

    public void solutionGASA(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout / 2;
        this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];

        for (int i = 0; i < this.timeout; i++) {
            this.ga = new GeneticAlgorithm();
//            System.out.println(this.crossoverRate + " " + this.mutationRate);
            this.ga.getSolution(initialPop, mobilKerja, mobilMasuk, matriksTipeOption, this.crossoverRate, this.mutationRate);

            if (i >= this.timeout - jmlKromDiambil) {
                this.kromTerbaikTerakhir[i - (this.timeout - jmlKromDiambil)] = this.ga.populasi.kromTerbaik;
            }
        }
        this.hasilTerbaik = this.ga.populasi.kromTerbaik;

        if (this.kromTerbaikTerakhir.length > 2) {
            this.kromTerbaikSA = new Kromosom[this.timeout];
            for (int i = 0; i < this.timeout; i++) {
                this.sa = new SimulatedAnnealing();
                this.sa.inisialisasiVariabel(this.kromTerbaikTerakhir, this.acceptanceProbability);
                this.sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);
                this.kromTerbaikSA[i] = this.sa.initialSolution;
            }
            this.hasilTerbaik = this.cariKromosomHasilTerbaik();
        }

    }

    private Kromosom cariKromosomHasilTerbaik() {
        Kromosom hasil = this.kromTerbaikSA[0];
        for (Kromosom kromTerbaikSA1 : this.kromTerbaikSA) {
            if (kromTerbaikSA1.gagalDikerjakan < hasil.gagalDikerjakan) {
                hasil = kromTerbaikSA1;
            } else if (hasil.gagalDikerjakan == 0) {
                break;
            }
        }
        return hasil;
    }

}
