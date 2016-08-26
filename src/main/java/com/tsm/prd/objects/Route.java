package com.tsm.prd.objects;

import java.util.List;
import java.util.Objects;

public class Route implements AsArray {
    private String departureName;
    private String departureId;
    private String destinationName;
    private String destinationId;
    private String[] original;
    private List<OutputRoute> outputRoutes;

    public Route(String departureName, String destinationName, String[] original) {
        this.departureName = departureName;
        this.destinationName = destinationName;
        this.original = original;
    }

    public Route(String departureName, String departureId, String destinationName, String destinationId, String[] original,
            List<OutputRoute> outputRoutes) {
        this.departureName = departureName;
        this.departureId = departureId;
        this.destinationName = destinationName;
        this.destinationId = destinationId;
        this.original = original;
        this.outputRoutes = outputRoutes;
    }

    public String getDepartureId() {
        return departureId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public List<OutputRoute> getOutputRoutes() {
        return outputRoutes;
    }

    public void setDepartureName(String departureName) {
        this.departureName = departureName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDepartureName() {
        return departureName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String[] getOriginal() {
        return original;
    }

    @Override
    public String[] asArray() {
        return original;
    }

    @Override
    public String toString() {
        return departureName + ": " + destinationName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Route other = (Route) obj;
        return Objects.equals(this.departureName, other.departureName) && Objects.equals(this.destinationName, other.destinationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.departureName, this.destinationName);
    }
}
