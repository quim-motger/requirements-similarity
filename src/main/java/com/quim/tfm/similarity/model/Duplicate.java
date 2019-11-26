package com.quim.tfm.similarity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Duplicate implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String req1Id;
    private String req2Id;

    @Transient
    private Double score;
    private DuplicateTag tag;

    private double wordOverlapScore;
    private double unigramMatchScore;
    private double bigramMatchScore;
    private double subjectMatchScore;
    private double subjectVerbMatchScore;
    private double objectVerbMatchScore;
    private double nounMatchScore;
    private double nameEntityScore;

    public Duplicate() {

    }

    public Duplicate(String req1Id, String req2Id, double score) {
        this.req1Id = req1Id;
        this.req2Id = req2Id;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public DuplicateTag getTag() {
        return tag;
    }

    public void setTag(DuplicateTag tag) {
        this.tag = tag;
    }

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

    public double getSubjectMatchScore() {
        return subjectMatchScore;
    }

    public void setSubjectMatchScore(double subjectMatchScore) {
        this.subjectMatchScore = subjectMatchScore;
    }

    public double getSubjectVerbMatchScore() {
        return subjectVerbMatchScore;
    }

    public void setSubjectVerbMatchScore(double subjectVerbMatchScore) {
        this.subjectVerbMatchScore = subjectVerbMatchScore;
    }

    public double getObjectVerbMatchScore() {
        return objectVerbMatchScore;
    }

    public void setObjectVerbMatchScore(double objectVerbMatchScore) {
        this.objectVerbMatchScore = objectVerbMatchScore;
    }

    public double getNounMatchScore() {
        return nounMatchScore;
    }

    public void setNounMatchScore(double nounMatchScore) {
        this.nounMatchScore = nounMatchScore;
    }

    public double getNameEntityScore() {
        return nameEntityScore;
    }

    public void setNameEntityScore(double nameEntityScore) {
        this.nameEntityScore = nameEntityScore;
    }
}
