package com.quim.tfm.similarity.model;

public enum DuplicateTag {

    DUPLICATE,
    NOT_DUPLICATE;

    public int getValue() {
        return this.equals(DuplicateTag.DUPLICATE) ? 1 : 0;
    }

    public static DuplicateTag fromValue(int val) {
        return val == 1 ? DUPLICATE : NOT_DUPLICATE;
    }

}
