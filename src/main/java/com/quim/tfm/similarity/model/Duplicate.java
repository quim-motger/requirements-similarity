package com.quim.tfm.similarity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Duplicate implements Serializable {

    private String req1Id;
    private String req2Id;
    private double score;

    public Duplicate() {

    }

    public Duplicate(String req1Id, String req2Id, double score) {
        this.req1Id = req1Id;
        this.req2Id = req2Id;
        this.score = score;
    }

    public String getReq1Id() {
        return req1Id;
    }

    public void setReq1Id(String req1Id) {
        this.req1Id = req1Id;
    }

    public String getReq2Id() {
        return req2Id;
    }

    public void setReq2Id(String req2Id) {
        this.req2Id = req2Id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
