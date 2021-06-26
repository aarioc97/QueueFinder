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
public class HybridGASA {

    //50% solusi terbaik terakhir dari GA dimasukkan ke SA
    public GeneticAlgorithm ga;
    public SimulatedAnnealing sa;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    private Kromosom[] kromTerbaikTerakhir;
    private int timeout;

    public HybridGASA(Kromosom[] initialPop, int timeout) {
        this.initialPop = initialPop;
        this.ga = new GeneticAlgorithm();
        this.sa = new SimulatedAnnealing();
        this.timeout = timeout;
    }

    public void solutionGASA(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {
        int jmlKromDiambil = this.timeout / 2;
        this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];

        for (int i = 0; i < this.timeout; i++) {
            this.ga.getSolution(initialPop, mobilKerja, mobilMasuk, matriksTipeOption);

            if (i >= this.timeout - jmlKromDiambil) {
                this.kromTerbaikTerakhir[i - (this.timeout - jmlKromDiambil)] = this.ga.kromTerbaik;
            }

            this.hasilTerbaik = this.ga.populasi.kromTerbaik;
        }

        if (this.kromTerbaikTerakhir.length > jmlKromDiambil) {
            for (int i = 0; i < this.timeout; i++) {
                sa.inisialisasiVariabel(this.kromTerbaikTerakhir);
                sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);
                this.hasilTerbaik = this.sa.initialSolution;
            }
        }

    }

}
