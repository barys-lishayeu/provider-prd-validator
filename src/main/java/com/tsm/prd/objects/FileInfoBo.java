package com.tsm.prd.objects;

public interface FileInfoBo {
    FileInfo withBoFileName(String boFileName);
    String getBoFileName();
    void setBoHeaders(String[] boHeaders);
    String[] getBoHeaders();
}
