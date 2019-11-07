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
    @Column(length = 35000, columnDefinition = "LONGTEXT")
    private String summary;
    @NotNull
    @Column(length = 2000000, columnDefinition = "LONGTEXT")
    private String description;

    private String priority;
    private String type;
    private String project;
    private String[] component;
    private String[] version;

    @Column(length = 35000, columnDefinition = "LONGTEXT")
    private String[] summaryTokens;
    @Column(length = 2000000, columnDefinition = "LONGTEXT")
    private String[] descriptionTokens;

    public Requirement() {
    }

    public Requirement(@NotNull String id, @NotNull String summary, @NotNull String description, String priority,
                       String type, String project, String[] component, String[] version) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.priority = priority;
        this.type = type;
        this.project = project;
        this.component = component;
        this.version = version;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setComponent(String[] component) {
        this.component = component;
    }

    public void setVersion(String[] version) {
        this.version = version;
    }
}
