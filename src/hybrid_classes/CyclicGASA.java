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
public class CyclicGASA {

    public GeneticAlgorithm ga;
    public SimulatedAnnealing sa;
    public Kromosom[] initialPop;
    public Kromosom hasilTerbaik;
    public Kromosom[] kromTerbaikTerakhir;
    private int timeout, timeoutCyclic, jmlKromDiambil;

    public CyclicGASA(Kromosom[] initialPop, int timeout, int timeoutCyclic) {
        this.initialPop = initialPop;
        this.sa = new SimulatedAnnealing();
        this.ga = new GeneticAlgorithm();
        this.timeout = timeout;
        this.timeoutCyclic = timeoutCyclic;
    }

    public void solutionCyclic(int[] mobilKerja, int[] mobilMasuk, int[][] matriksTipeOption) {

        for (int i = 0; i < this.timeoutCyclic; i++) {
            if (i == 0 || i / 2 == 0 && this.kromTerbaikTerakhir.length > this.jmlKromDiambil) {
                this.ga.populasi.kromosom = this.initialPop;
                this.jmlKromDiambil = this.timeout / 10;
                this.kromTerbaikTerakhir = new Kromosom[jmlKromDiambil];

                for (int j = 0; j < this.timeout; j++) {
                    this.ga.getSolution(initialPop, mobilKerja, mobilMasuk, matriksTipeOption);

                    if (i >= this.timeout - jmlKromDiambil) {
                        this.kromTerbaikTerakhir[i - (this.timeout - jmlKromDiambil)] = this.ga.kromTerbaik;
                    }

                    this.hasilTerbaik = this.ga.populasi.kromTerbaik;
                }
                this.initialPop = this.kromTerbaikTerakhir;
            } else if (this.kromTerbaikTerakhir.length > this.jmlKromDiambil) {
                for (int j = 0; j < this.timeout; j++) {
                    sa.inisialisasiVariabel(this.initialPop);
                    sa.getSolution(mobilKerja, mobilMasuk, matriksTipeOption);

                    this.kromTerbaikTerakhir = this.sa.populasi.kromosom;
                    this.hasilTerbaik = this.sa.initialSolution;
                }
                this.initialPop = this.kromTerbaikTerakhir;
            }
        }

    }

}
