package com.quim.tfm.similarity.model;

public class Token {

    private String lemma;
    private String pos;

    public Token(String lemma, String pos) {
        this.lemma = lemma;
        this.pos = pos;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }
}
