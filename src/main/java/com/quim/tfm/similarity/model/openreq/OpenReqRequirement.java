package com.quim.tfm.similarity.model.openreq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenReqRequirement implements Serializable {

    private String id;
    private String name;
    private String text;
    private List<OpenReqRequirementPart> requirementParts;

    public OpenReqRequirement() {
        this.requirementParts = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<OpenReqRequirementPart> getRequirementParts() {
        return requirementParts;
    }

    public void setRequirementParts(List<OpenReqRequirementPart> requirementParts) {
        this.requirementParts = requirementParts;
    }
}
