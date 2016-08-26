package com.epam.utils.objects;

import com.epam.utils.objects.header.HeaderIndex;

public interface FileInfoPrdPartner {
    FileInfo withPartnerFileName(String partnerFileName);

    FileInfo withPartnerIndexes(int depIndex, int... partnerIndexes);

    String getPartnerFileName();

    HeaderIndex getPartnerIndexes();

    void setPartnerHeaders(String[] partnerHeaders);

    String[] getPartnerHeaders();

}
