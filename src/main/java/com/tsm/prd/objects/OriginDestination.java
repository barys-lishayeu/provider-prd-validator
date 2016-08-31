package com.tsm.prd.objects;


import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

public class OriginDestination {
    private static final Joiner JOINER = Joiner.on(", ").skipNulls();
    private String country;
    private String destination;
    private String resort;

    public OriginDestination(String country, String destination, String resort) {
        this.country = country;
        this.destination = destination;
        this.resort = resort;
    }

    @Override
    public String toString() {
        String result = "";
        if (!Strings.isNullOrEmpty(country)) {
            result = JOINER.join(country, result).trim();
        }
        if (!Strings.isNullOrEmpty(destination)) {
            result = JOINER.join(destination, result).trim();
        }
        if (!Strings.isNullOrEmpty(resort)) {
            result = JOINER.join(resort, result).trim();
        }

        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public String getCountry() {
        return country;
    }

    public String getDestination() {
        return destination;
    }

    public String getResort() {
        return resort;
    }


    public boolean isCountryOnly() {
        return StringUtils.isNotEmpty(getCountry())
                && StringUtils.isEmpty(getDestination())
                && StringUtils.isEmpty(getResort());
    }
}
