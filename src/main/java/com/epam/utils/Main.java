package com.epam.utils;

import com.epam.utils.holidays.BritishAirwayPrdComparator;
import com.epam.utils.holidays.TeletextPrdComparator;
import com.epam.utils.holidays.ThomsonPrdComparator;
import com.epam.utils.objects.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static final String ROOT = "/d_drive/kadri/Teletext";

    private Main() {
    }

    public static void main(String[] args) {
        new TeletextPrdComparator().run(FileInfo.create().
                withBoFileName("holidays-routes-Teletext-Holidays-(08-10-2016).csv").
                withPartnerFileName("Departure_Airport_Destination_List.csv").withPartnerIndexes(1, 2, 3, 4).
                withMappingsFileName("Teletext-Holidays-(08-10-2016).csv"));

        new BritishAirwayPrdComparator().run(FileInfo.create().
                withBoFileName("holidays-routes-British-Airways-live-(08-05-2016).csv").
                withPartnerFileName("BA_PRD_02_03_2016.csv").withPartnerIndexes(12, 0, 1, 2).
                withMappingsFileName("British-Airways-mappings-live-(08-05-2016).csv"));

        new ThomsonPrdComparator().run(FileInfo.create().
                withBoFileName("holidays-routes-Thomson-(08-10-2016).csv").
                withPartnerFileName("Thomson_PRD_v1_6.csv").withPartnerIndexes(12, 0, 1, 2).
                withMappingsFileName("Thomson-mappings(08-11-2016).csv"));
    }
}
