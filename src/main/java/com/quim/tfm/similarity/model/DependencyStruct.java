package com.quim.tfm.similarity.model;

import edu.stanford.nlp.trees.TypedDependency;

import java.util.List;

public class DependencyStruct {

    private List<String> subjects;
    private List<TypedDependency> subjectVerbDependencies;
    private List<TypedDependency> objectVerbDependencies;
    private List<TypedDependency> nounDependencies;

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<TypedDependency> getSubjectVerbDependencies() {
        return subjectVerbDependencies;
    }

    public void setSubjectVerbDependencies(List<TypedDependency> subjectVerbDependencies) {
        this.subjectVerbDependencies = subjectVerbDependencies;
    }

    public List<TypedDependency> getObjectVerbDependencies() {
        return objectVerbDependencies;
    }

    public void setObjectVerbDependencies(List<TypedDependency> objectVerbDependencies) {
        this.objectVerbDependencies = objectVerbDependencies;
    }

    public List<TypedDependency> getNounDependencies() {
        return nounDependencies;
    }

    public void setNounDependencies(List<TypedDependency> nounDependencies) {
        this.nounDependencies = nounDependencies;
    }
}
