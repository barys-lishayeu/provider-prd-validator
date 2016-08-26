package com.tsm.prd.holidays;

import au.com.bytecode.opencsv.CSVReader;
import com.tsm.prd.PrdComparator;
import com.tsm.prd.objects.Airports;
import com.tsm.prd.objects.FileInfoPrdPartner;
import com.tsm.prd.objects.header.HeaderIndex;
import com.tsm.prd.objects.Route;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BritishAirwayPrdComparator extends PrdComparator {
    private static final Logger LOGGER = LoggerFactory.getLogger(BritishAirwayPrdComparator.class);

    @Override
    public String getProviderName() {
        return "british_airway";
    }

    @Override
    public ListMultimap<String, Route> loadPartnerRoutes(final FileInfoPrdPartner info) {
        final ListMultimap<String, Route> routes = ArrayListMultimap.create();
        final HeaderIndex headerIndex = info.getPartnerIndexes();

        try (final CSVReader importReader = new CSVReader(new BufferedReader(new FileReader(new File(providerPath() + info.getPartnerFileName()))))) {
            info.setPartnerHeaders(importReader.readNext());

            for (String[] line = importReader.readNext(); line != null; line = importReader.readNext()) {
                final String dirtyDeparture = line[headerIndex.getDepIndex()];
                if (Strings.isNullOrEmpty(dirtyDeparture)) {
                    continue;
                }

                final String destination = getDestinationByIndex(line, headerIndex);

                for (String airportStr : COMMA_SPLITTER.split(dirtyDeparture)) {
                    String departureAirport = airportStr;
                    if (StringUtils.isNumeric(airportStr)) {
                        departureAirport = Airports.getAirportByNumber(airportStr);
                    }

                    Route route = new Route(departureAirport, destination, line);
                    cleanPartnerRoute(route);
                    routes.put(departureAirport, route);
                }
            }

        } catch (IOException e) {
            LOGGER.error("Can't load the file {}", info.getPartnerFileName());
        }
        return routes;
    }

}
