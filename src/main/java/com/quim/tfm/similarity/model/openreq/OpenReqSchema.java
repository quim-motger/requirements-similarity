package com.quim.tfm.similarity.model.openreq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OpenReqSchema implements Serializable {

    private List<OpenReqRequirement> requirements;
    private List<OpenReqProject> projects;
    private List<OpenReqDependency> dependencies;

    public OpenReqSchema() {
        this.requirements = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.dependencies = new ArrayList<>();
    }

    public List<OpenReqRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<OpenReqRequirement> requirements) {
        this.requirements = requirements;
    }

    public List<OpenReqProject> getProjects() {
        return projects;
    }

    public void setProjects(List<OpenReqProject> projects) {
        this.projects = projects;
    }

    public List<OpenReqDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<OpenReqDependency> dependencies) {
        this.dependencies = dependencies;
    }
}
