package com.quim.tfm.similarity.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quim.tfm.similarity.model.Priority;

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
    private Priority priority;
    private String type;
    private String project;
    private String[] components;
    private String[] versions;
    @Column(length = 35000, columnDefinition = "LONGTEXT")
    private String[] summaryTokens;
    @Column(length = 2000000, columnDefinition = "LONGTEXT")
    private String[] descriptionTokens;

    public Requirement() {
    }

    public Requirement(@NotNull String id, @NotNull String summary, @NotNull String description, Priority priority,
                       String type, String project, String[] components, String[] versions) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.priority = priority;
        this.type = type;
        this.project = project;
        this.components = components;
        this.versions = versions;
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
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

    public String[] getComponents() {
        return components;
    }

    public void setComponents(String[] components) {
        this.components = components;
    }

    public String[] getVersions() {
        return versions;
    }

    public void setVersions(String[] versions) {
        this.versions = versions;
    }
}
