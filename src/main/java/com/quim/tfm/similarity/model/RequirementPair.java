package com.quim.tfm.similarity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quim.tfm.similarity.entity.Requirement;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequirementPair implements Serializable {

    private Requirement req1;
    private Requirement req2;

    public RequirementPair() {

    }

    public RequirementPair(Requirement req1, Requirement req2) {
        this.req1 = req1;
        this.req2 = req2;
    }

    public Requirement getReq1() {
        return req1;
    }

    public void setReq1(Requirement req1) {
        this.req1 = req1;
    }

    public Requirement getReq2() {
        return req2;
    }

    public void setReq2(Requirement req2) {
        this.req2 = req2;
    }
}
