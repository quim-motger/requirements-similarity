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

    private double wordOverlapNameScore;
    private double unigramMatchNameScore;
    private double bigramMatchNameScore;
    private double subjectMatchNameScore;
    private double subjectVerbMatchNameScore;
    private double objectVerbMatchNameScore;
    private double nounMatchNameScore;
    private double nameEntityNameScore;

    private double wordOverlapTextScore;
    private double unigramMatchTextScore;
    private double bigramMatchTextScore;
    private double subjectMatchTextScore;
    private double subjectVerbMatchTextScore;
    private double objectVerbMatchTextScore;
    private double nounMatchTextScore;
    private double nameEntityTextScore;

    public Duplicate() {

    }

    public Duplicate(String req1Id, String req2Id, Double score) {
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

    public double getWordOverlapNameScore() {
        return wordOverlapNameScore;
    }

    public void setWordOverlapNameScore(double wordOverlapNameScore) {
        this.wordOverlapNameScore = wordOverlapNameScore;
    }

    public double getUnigramMatchNameScore() {
        return unigramMatchNameScore;
    }

    public void setUnigramMatchNameScore(double unigramMatchNameScore) {
        this.unigramMatchNameScore = unigramMatchNameScore;
    }

    public double getBigramMatchNameScore() {
        return bigramMatchNameScore;
    }

    public void setBigramMatchNameScore(double bigramMatchNameScore) {
        this.bigramMatchNameScore = bigramMatchNameScore;
    }

    public double getSubjectMatchNameScore() {
        return subjectMatchNameScore;
    }

    public void setSubjectMatchNameScore(double subjectMatchNameScore) {
        this.subjectMatchNameScore = subjectMatchNameScore;
    }

    public double getSubjectVerbMatchNameScore() {
        return subjectVerbMatchNameScore;
    }

    public void setSubjectVerbMatchNameScore(double subjectVerbMatchNameScore) {
        this.subjectVerbMatchNameScore = subjectVerbMatchNameScore;
    }

    public double getObjectVerbMatchNameScore() {
        return objectVerbMatchNameScore;
    }

    public void setObjectVerbMatchNameScore(double objectVerbMatchNameScore) {
        this.objectVerbMatchNameScore = objectVerbMatchNameScore;
    }

    public double getNounMatchNameScore() {
        return nounMatchNameScore;
    }

    public void setNounMatchNameScore(double nounMatchNameScore) {
        this.nounMatchNameScore = nounMatchNameScore;
    }

    public double getNameEntityNameScore() {
        return nameEntityNameScore;
    }

    public void setNameEntityNameScore(double nameEntityNameScore) {
        this.nameEntityNameScore = nameEntityNameScore;
    }

    public double getWordOverlapTextScore() {
        return wordOverlapTextScore;
    }

    public void setWordOverlapTextScore(double wordOverlapTextScore) {
        this.wordOverlapTextScore = wordOverlapTextScore;
    }

    public double getUnigramMatchTextScore() {
        return unigramMatchTextScore;
    }

    public void setUnigramMatchTextScore(double unigramMatchTextScore) {
        this.unigramMatchTextScore = unigramMatchTextScore;
    }

    public double getBigramMatchTextScore() {
        return bigramMatchTextScore;
    }

    public void setBigramMatchTextScore(double bigramMatchTextScore) {
        this.bigramMatchTextScore = bigramMatchTextScore;
    }

    public double getSubjectMatchTextScore() {
        return subjectMatchTextScore;
    }

    public void setSubjectMatchTextScore(double subjectMatchTextScore) {
        this.subjectMatchTextScore = subjectMatchTextScore;
    }

    public double getSubjectVerbMatchTextScore() {
        return subjectVerbMatchTextScore;
    }

    public void setSubjectVerbMatchTextScore(double subjectVerbMatchTextScore) {
        this.subjectVerbMatchTextScore = subjectVerbMatchTextScore;
    }

    public double getObjectVerbMatchTextScore() {
        return objectVerbMatchTextScore;
    }

    public void setObjectVerbMatchTextScore(double objectVerbMatchTextScore) {
        this.objectVerbMatchTextScore = objectVerbMatchTextScore;
    }

    public double getNounMatchTextScore() {
        return nounMatchTextScore;
    }

    public void setNounMatchTextScore(double nounMatchTextScore) {
        this.nounMatchTextScore = nounMatchTextScore;
    }

    public double getNameEntityTextScore() {
        return nameEntityTextScore;
    }

    public void setNameEntityTextScore(double nameEntityTextScore) {
        this.nameEntityTextScore = nameEntityTextScore;
    }
}
