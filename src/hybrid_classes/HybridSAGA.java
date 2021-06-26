/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hybrid_classes;

import java.util.Random;
import geneticalgorithm_simulatedannealing.GeneticAlgorithm;
import gasa_component.Kromosom;
import geneticalgorithm_simulatedannealing.SimulatedAnnealing;

/**
 *
 * @author i15055
 */
public class HybridSAGA {

    public SimulatedAnnealing sa;
    public GeneticAlgorithm ga;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    private Kromosom[] kromTerbaikTerakhir;
    private int timeout;

    public HybridSAGA(Kromosom[] initialPop, int timeout) {
        this.initialPop = initialPop;
        this.sa = new SimulatedAnnealing();
        this.ga = new GeneticAlgorithm();
        this.timeout = timeout;
    }

    public void solutionSAGA(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout * 4 / 5;
        this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];

        for (int i = 0; i < this.timeout; i++) {
            sa.inisialisasiVariabel(this.initialPop);
            sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);

            if (i >= this.timeout - jmlKromDiambil) {
                this.sa.populasi.hitungFitness(mobilKerja, mobilMasuk, matriksTipeOption);
                this.kromTerbaikTerakhir[i - (this.timeout - jmlKromDiambil)] = this.sa.populasi.getKromosomTerbaik();
            }

            this.hasilTerbaik = this.sa.initialSolution;

        }

        if (this.kromTerbaikTerakhir.length > jmlKromDiambil) {
            for (int i = 0; i < this.timeout; i++) {
                this.ga.getSolution(this.kromTerbaikTerakhir, mobilKerja, mobilMasuk, matriksTipeOption);

                this.hasilTerbaik = this.ga.kromTerbaik;

            }
        }

    }

}
