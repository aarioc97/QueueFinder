/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csp_counter;

/**
 *
 * @author i15055
 */
public class CSPCounter {

    private double[] oNum, oMax, utilityScale;
    private int avgUtility;

    public CSPCounter(int jmlOption) {
        this.oNum = new double[jmlOption];
        this.oMax = new double[jmlOption];
        this.utilityScale = new double[jmlOption];
        this.avgUtility = 0;
    }

    private void countONum(int[] prodPerTipe, int[][] matriksTipeOption) {
        for (int i = 0; i < prodPerTipe.length; i++) {
            for (int j = 0; j < this.oNum.length; j++) {
                if (matriksTipeOption[i][j] == 1) {
                    this.oNum[j] += prodPerTipe[i];
                }
            }
        }
    }

    private void countOMax(int totalProd, int[] mobilKerja, int[] mobilMasuk) {
        for (int i = 0; i < this.oMax.length; i++) {
            this.oMax[i] = (totalProd * mobilKerja[i]) / mobilMasuk[i];
        }
    }

    private void countUtilityScale() {
        for (int i = 0; i < this.utilityScale.length; i++) {
            this.utilityScale[i] = this.oNum[i] / this.oMax[i];
        }
    }

    private double countAvgUtility() {
        double sumUtilityScale = 0;
        for (int i = 0; i < this.utilityScale.length; i++) {
            sumUtilityScale += this.utilityScale[i];
        }
        return sumUtilityScale / this.utilityScale.length;
    }

    public String countCSP(int[] prodPerTipe, int[][] matriksTipeOption, int totalProd, int[] mobilKerja, int[] mobilMasuk) {
        this.countONum(prodPerTipe, matriksTipeOption);
        this.countOMax(totalProd, mobilKerja, mobilMasuk);
        this.countUtilityScale();
        return String.format("%.2f", this.countAvgUtility());
    }

}
