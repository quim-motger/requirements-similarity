package com.quim.tfm.similarity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats implements Serializable {

    private int TP;
    private int TN;
    private int FP;
    private int FN;

    public Stats(int tp, int tn, int fp, int fn) {
        this.TP = tp;
        this.TN = tn;
        this.FP = fp;
        this.FN = fn;
    }

    public int getTP() {
        return TP;
    }

    public void setTP(int TP) {
        this.TP = TP;
    }

    public int getTN() {
        return TN;
    }

    public void setTN(int TN) {
        this.TN = TN;
    }

    public int getFP() {
        return FP;
    }

    public void setFP(int FP) {
        this.FP = FP;
    }

    public int getFN() {
        return FN;
    }

    public void setFN(int FN) {
        this.FN = FN;
    }

    public double getAccuracy() {
        return (double) (TN + TP) / (double) (TN + TP + FP + FN);
    }

}
