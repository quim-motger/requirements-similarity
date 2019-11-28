package com.quim.tfm.similarity.model.openreq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quim.tfm.similarity.model.DependencyType;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenReqDependency implements Serializable {

    private String fromid;
    private String toid;
    private DependencyType dependency_type;
    private Double dependency_score;
    private DependencyStatus status;

    public OpenReqDependency(String fromid, String toid, DependencyType dependency_type, Double dependency_score) {
        this.fromid = fromid;
        this.toid = toid;
        this.dependency_type = dependency_type;
        this.dependency_score = dependency_score;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public DependencyType getDependency_type() {
        return dependency_type;
    }

    public void setDependency_type(DependencyType dependency_type) {
        this.dependency_type = dependency_type;
    }

    public Double getDependency_score() {
        return dependency_score;
    }

    public void setDependency_score(Double dependency_score) {
        this.dependency_score = dependency_score;
    }

    public DependencyStatus getStatus() {
        return status;
    }

    public void setStatus(DependencyStatus status) {
        this.status = status;
    }
}
