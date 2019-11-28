package com.quim.tfm.similarity.model;

import edu.stanford.nlp.trees.GrammaticalStructure;

import java.util.List;

public class FEPreprocessData {

    private String[] tokens;
    private String[] lemmas;
    private String[] posTags;
    private List<GrammaticalStructure> grammaticalStructureList;

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

    public List<GrammaticalStructure> getGrammaticalStructureList() {
        return grammaticalStructureList;
    }

    public void setGrammaticalStructureList(List<GrammaticalStructure> grammaticalStructureList) {
        this.grammaticalStructureList = grammaticalStructureList;
    }
}
