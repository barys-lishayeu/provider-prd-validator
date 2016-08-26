package com.tsm.prd.objects;

import com.tsm.prd.objects.header.HeaderIndex;

public class FileInfo implements FileInfoBo, FileInfoPrdPartner, FileInfoMappings {
    private String boFileName;
    private String partnerFileName;
    private HeaderIndex partnerIndexes;
    private String mappingsFileName;
    private String[] boHeaders;
    private String[] partnerHeaders;
    private String[] mappingsHeaders;

    public static FileInfo create() {
        return new FileInfo();
    }

    @Override
    public FileInfo withBoFileName(String boFileName) {
        this.boFileName = boFileName;
        return this;
    }

    @Override
    public FileInfo withPartnerFileName(String partnerFileName) {
        this.partnerFileName = partnerFileName;
        return this;
    }

    @Override
    public FileInfo withPartnerIndexes(int depIndex, int... partnerIndexes) {
        this.partnerIndexes = HeaderIndex.create(depIndex, partnerIndexes);
        return this;
    }

    @Override
    public FileInfo withMappingsFileName(String mappingsFileName) {
        this.mappingsFileName = mappingsFileName;
        return this;
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
    public HeaderIndex getPartnerIndexes() {
        return partnerIndexes;
    }

    @Override
    public String getMappingsFileName() {
        return mappingsFileName;
    }

    @Override
    public void setBoHeaders(String[] boHeaders) {
        this.boHeaders = boHeaders;
    }

    @Override
    public String[] getBoHeaders() {
        return boHeaders;
    }

    @Override
    public void setPartnerHeaders(String[] partnerHeaders) {
        this.partnerHeaders = partnerHeaders;
    }

    @Override
    public String[] getPartnerHeaders() {
        return partnerHeaders;
    }

    @Override
    public void setMappingsHeaders(String[] mappingsHeaders) {
        this.mappingsHeaders = mappingsHeaders;
    }

    @Override
    public String[] getMappingsHeaders() {
        return mappingsHeaders;
    }
}
