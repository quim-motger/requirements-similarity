package com.quim.tfm.similarity.model;

import java.util.List;

public class DependencyTreeNode {

    private List<DependencyTreeNode> childList;
    private String lemma;
    //private Dependency dependency;

    public List<DependencyTreeNode> getChildList() {
        return childList;
    }

    public void setChildList(List<DependencyTreeNode> childList) {
        this.childList = childList;
    }


    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    /*public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }*/
}
