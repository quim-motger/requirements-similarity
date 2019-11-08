package com.quim.tfm.similarity.model;

public class TrainTripletBM25F {

    private String q;
    private String rel;
    private String irrel;

    public TrainTripletBM25F(String q, String rel, String irrel) {
        this.q = q;
        this.rel = rel;
        this.irrel = irrel;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getIrrel() {
        return irrel;
    }

    public void setIrrel(String irrel) {
        this.irrel = irrel;
    }
}
