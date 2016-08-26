package com.epam.utils.objects;

public interface FileInfoBo {
    FileInfo withBoFileName(String boFileName);
    String getBoFileName();
    void setBoHeaders(String[] boHeaders);
    String[] getBoHeaders();
}
