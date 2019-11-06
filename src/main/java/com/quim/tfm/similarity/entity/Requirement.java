package com.quim.tfm.similarity.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Requirement implements Serializable {

    @Id
    @NotNull
    private String id;
    @NotNull
    @Column(length = 35000, columnDefinition = "text")
    private String summary;
    @NotNull
    @Column(length = 50000, columnDefinition = "text")
    private String description;
    @Column(length = 35000, columnDefinition = "text")
    private String[] summaryTokens;
    @Column(length = 50000, columnDefinition = "text")
    private String[] descriptionTokens;

    public Requirement() {
    }

    public Requirement(String id, String summary, String description) {
        this.id = id;
        this.summary = summary;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getSummaryTokens() {
        return summaryTokens;
    }

    public void setSummaryTokens(String[] summaryTokens) {
        this.summaryTokens = summaryTokens;
    }

    public String[] getDescriptionTokens() {
        return descriptionTokens;
    }

    public void setDescriptionTokens(String[] descriptionTokens) {
        this.descriptionTokens = descriptionTokens;
    }
}
