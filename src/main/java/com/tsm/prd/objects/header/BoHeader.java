package com.tsm.prd.objects.header;

import static com.google.common.base.Preconditions.checkNotNull;

public enum BoHeader {
    ID("_id"),
    INPUT_DEPARTURE_ID("inputDepartureId"),
    INPUT_DEPARTURE_NAME("inputDepartureName"),
    INPUT_DESTINATION_ID("inputDestinationId"),
    INPUT_DESTINATION_NAME("inputDestinationName"),
    IS_ACTIVE("isActive"),
    OUTPUT_ROUTES_PRIORITY("outputRoutes1_priority"),
    OUTPUT_ROUTES_OUTPUT_DEPARTURE_ID("outputRoutes1_outputDepartureId"),
    OUTPUT_ROUTES_OUTPUT_DESTINATION_ID("outputRoutes1_outputDestinationId"),
    ANY("any");

    private String name;

    BoHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BoHeader getByValue(final String value) {
        checkNotNull(value);

        for (BoHeader boHeader : BoHeader.values()) {
            if (value.equalsIgnoreCase(boHeader.getName())) {
                return boHeader;
            }
        }
        return ANY;
    }
}
