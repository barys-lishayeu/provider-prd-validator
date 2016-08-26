package com.tsm.prd.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tsm.prd.objects.ConfigBo;
import com.tsm.prd.objects.ConfigMappings;
import com.tsm.prd.objects.ConfigPrdPartner;

public class ProviderConfig implements ConfigBo, ConfigPrdPartner, ConfigMappings {
    private String providerName;
    private String providerClass;
    private String boFileName;
    private String partnerFileName;
    private int departureIndex;
    private int[] destinationIndexes;
    private String mappingsFileName;
    @JsonIgnore
    private String[] boHeaders;
    @JsonIgnore
    private String[] partnerHeaders;
    @JsonIgnore
    private String[] mappingsHeaders;

    public String getProviderName() {
        return providerName;
    }

    public String getProviderClass() {
        return providerClass;
    }

    @Override
    public String[] getPartnerHeaders() {
        return partnerHeaders;
    }

    @Override
    public String getBoFileName() {
        return boFileName;
    }

    @Override
    public String getPartnerFileName() {
        return partnerFileName;
    }

    @Override
    public int getDepartureIndex() {
        return departureIndex;
    }

    @Override
    public int[] getDestinationIndexes() {
        return destinationIndexes;
    }

    @Override
    public String getMappingsFileName() {
        return mappingsFileName;
    }

    @Override
    public String[] getBoHeaders() {
        return boHeaders;
    }

    @Override
    public String[] getMappingsHeaders() {
        return mappingsHeaders;
    }

    @Override
    public void setBoHeaders(String[] boHeaders) {
        this.boHeaders = boHeaders;
    }

    @Override
    public void setMappingsHeaders(String[] mappingsHeaders) {
        this.mappingsHeaders = mappingsHeaders;
    }

    @Override
    public void setPartnerHeaders(String[] partnerHeaders) {
        this.partnerHeaders = partnerHeaders;
    }


}
