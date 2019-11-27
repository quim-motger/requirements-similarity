package com.quim.tfm.similarity.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Priority {

    @JsonProperty("Not Evaluated") NE(0),
    @JsonProperty("P0: Blocker") P0(1),
    @JsonProperty("P1: Critical") P1(2),
    @JsonProperty("P2: Important") P2(3),
    @JsonProperty("P3: Somewhat important") P3(4),
    @JsonProperty("P4: Low") P4(5),
    @JsonProperty("P5: Not important") P5(6),
    @JsonEnumDefaultValue NOT_DEFINED(-1);

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

    public static Priority fromString(String name) {
        switch(name) {
            case "Not Evaluated":
                return NE;
            case "P0: Blocker":
                return P0;
            case "P1: Critical":
                return P1;
            case "P2: Important":
                return P2;
            case "P3: Somewhat important":
                return P3;
            case "P4: Low":
                return P4;
            case "P5: Not important":
                return P5;
            default:
                return NOT_DEFINED;
        }
    }

    @JsonCreator
    public static Priority fromValue(String name) {
        for (Priority b : Priority.values()) {
            switch (b.value) {
                case -1:
                    return NOT_DEFINED;
                case 0:
                    return NE;
                case 1:
                    return P0;
                case 2:
                    return P1;
                case 3:
                    return P2;
                case 4:
                    return P3;
                case 5:
                    return P4;
                case 6:
                    return P5;
            }
        }
        return NOT_DEFINED;
    }

}
