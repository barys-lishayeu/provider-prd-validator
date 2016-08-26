package com.tsm.prd.objects.header;

import java.util.HashMap;
import java.util.Map;

public class BoHeaderHolder {
    private static BoHeaderHolder instance = null;
    private static final Map<BoHeader, Integer> HEADER_MAP = new HashMap<>();

    private BoHeaderHolder() {
    }

    public static void analysis(String[] lines) {
        if (instance == null) {
            instance = new BoHeaderHolder();
        }

        for (int i = 0; i < lines.length; i++) {
            final BoHeader rowHeader = BoHeader.getByValue(lines[i]);

            if (BoHeader.ANY != rowHeader) {
                HEADER_MAP.put(rowHeader, i);
            }
        }

    }

    public static Integer getIndex(BoHeader headerName) {
        return HEADER_MAP.get(headerName);
    }
}
