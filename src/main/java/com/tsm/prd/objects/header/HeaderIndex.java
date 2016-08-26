package com.tsm.prd.objects.header;

public class HeaderIndex {
    private int depIndex;
    private int[] desIndex;

    private HeaderIndex(int depIndex, int ... desIndex) {
        this.depIndex = depIndex;
        this.desIndex = desIndex;
    }

    public static HeaderIndex create(int depIndex, int ... desIndex) {
        return new HeaderIndex(depIndex, desIndex);
    }

    public int getDepIndex() {
        return depIndex;
    }

    public int getDesIndex() {
        return desIndex[0];
    }

    public int[] getDesIndexes() {
        return desIndex;
    }
}
