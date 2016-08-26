package com.tsm.prd.objects;

public class OutputRoute {
    private String outputDepartureId;
    private String outputDestinationId;

    public OutputRoute(String outputDepartureId, String outputDestinationId) {
        this.outputDepartureId = outputDepartureId;
        this.outputDestinationId = outputDestinationId;
    }

    public String getOutputDepartureId() {
        return outputDepartureId;
    }

    public String getOutputDestinationId() {
        return outputDestinationId;
    }
}
