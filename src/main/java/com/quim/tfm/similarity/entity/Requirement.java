package com.quim.tfm.similarity.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quim.tfm.similarity.model.Priority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "requirement", indexes = {@Index(name = "id_index", columnList="id", unique = true),
        @Index(name = "project_index", columnList="project")})
public class Requirement implements Serializable {

    @Id
    @NotNull
    private String id;
    @NotNull
    @Column(columnDefinition = "LONGTEXT")
    private String summary;
    @NotNull
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private Priority priority;
    private String type;
    private String project;
    private String[] components;
    @Column(columnDefinition = "LONGTEXT")
    private String[] versions;

    //DATA FOR BM25F
    @Column(columnDefinition = "LONGTEXT")
    private String[] summaryTokens;
    @Column(columnDefinition = "LONGTEXT")
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
        return Arrays.stream(summaryTokens).filter(t -> !t.equals(".")).collect(Collectors.toList()).toArray(new String[0]);
    }

    public String[] getSummaryTokensWithSentenceBoundaries() {
        return summaryTokens;
    }

    public void setSummaryTokens(String[] summaryTokens) {
        this.summaryTokens = summaryTokens;
    }

    public String[] getDescriptionTokens() {
        return Arrays.stream(descriptionTokens).filter(t -> !t.equals(".")).collect(Collectors.toList()).toArray(new String[0]);
    }

    public String[] getDescriptionTokensWithSentenceBoundaries() {
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
