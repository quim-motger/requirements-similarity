package com.quim.tfm.similarity.model;

public class FEPreprocessData {

    private String[] tokens;
    private String[] lemmas;
    private String[] posTags;
    private DependencyTreeNode node;

    public String[] getTokens() {
        return tokens;
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }

    public String[] getLemmas() {
        return lemmas;
    }

    public void setLemmas(String[] lemmas) {
        this.lemmas = lemmas;
    }

    public String[] getPosTags() {
        return posTags;
    }

    public void setPosTags(String[] posTags) {
        this.posTags = posTags;
    }

    public DependencyTreeNode getNode() {
        return node;
    }

    public void setNode(DependencyTreeNode node) {
        this.node = node;
    }
}
