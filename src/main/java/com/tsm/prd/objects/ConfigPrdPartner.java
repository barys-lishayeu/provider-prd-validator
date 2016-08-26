package com.tsm.prd.objects;

public interface ConfigPrdPartner {
    String getPartnerFileName();

    void setPartnerHeaders(String[] partnerHeaders);

    String[] getPartnerHeaders();

    int getDepartureIndex();

    int[] getDestinationIndexes();
}
