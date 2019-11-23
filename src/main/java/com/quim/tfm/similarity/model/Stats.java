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

    public Stats() {
        this.TP = 0;
        this.TN = 0;
        this.FP = 0;
        this.FN = 0;
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

    public void incrementTP() {
        ++TP;
    }

    public void incrementTN() {
        ++TN;
    }

    public void incrementFP() {
        ++FP;
    }

    public void incrementFN() {
        ++FN;
    }

    public double getAccuracy() {
        return (double) (TN + TP) / (double) (TN + TP + FP + FN);
    }
}
