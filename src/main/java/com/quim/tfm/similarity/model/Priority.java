package com.quim.tfm.similarity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Priority {

    @JsonProperty("Not Evaluated") NE(0),
    @JsonProperty("P0: Blocker") P0(1),
    @JsonProperty("P1: Critical") P1(2),
    @JsonProperty("P2: Important") P2(3),
    @JsonProperty("P3: Somewhat important") P3(4),
    @JsonProperty("P4: Low") P4(5),
    @JsonProperty("P5: Not important") P5(6);

    private int value;

    Priority(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
