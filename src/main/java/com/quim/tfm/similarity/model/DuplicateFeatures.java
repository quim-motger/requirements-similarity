package com.quim.tfm.similarity.model;

public class DuplicateFeatures {

    private double wordOverlapScore;
    private double unigramMatchScore;
    private double bigramMatchScore;

    public double getWordOverlapScore() {
        return wordOverlapScore;
    }

    public void setWordOverlapScore(double wordOverlapScore) {
        this.wordOverlapScore = wordOverlapScore;
    }

    public double getUnigramMatchScore() {
        return unigramMatchScore;
    }

    public void setUnigramMatchScore(double unigramMatchScore) {
        this.unigramMatchScore = unigramMatchScore;
    }

    public double getBigramMatchScore() {
        return bigramMatchScore;
    }

    public void setBigramMatchScore(double bigramMatchScore) {
        this.bigramMatchScore = bigramMatchScore;
    }
}
