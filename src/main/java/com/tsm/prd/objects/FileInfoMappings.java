package com.tsm.prd.objects;

public interface FileInfoMappings {
    FileInfo withMappingsFileName(String mappingsFileName);

    String getMappingsFileName();

    void setMappingsHeaders(String[] mappingsHeaders);

    String[] getMappingsHeaders();

}
