package com.tsm.prd.holidays;

import au.com.bytecode.opencsv.CSVReader;
import com.tsm.prd.PrdComparatorImpl;
import com.tsm.prd.objects.Airports;
import com.tsm.prd.objects.ConfigPrdPartner;
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

public class ThomsonPrdComparator extends PrdComparatorImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThomsonPrdComparator.class);

    @Override
    public ListMultimap<String, Route> loadPartnerRoutes(final ConfigPrdPartner info) {
        final ListMultimap<String, Route> routes = ArrayListMultimap.create();

        try (final CSVReader importReader = new CSVReader(new BufferedReader(new FileReader(new File(providerPath() + info.getPartnerFileName()))))) {
            info.setPartnerHeaders(importReader.readNext());

            for (String[] line = importReader.readNext(); line != null; line = importReader.readNext()) {
                final String dirtyDeparture = line[info.getDepartureIndex()];
                if (Strings.isNullOrEmpty(dirtyDeparture)) {
                    continue;
                }

                final String destination = getDestinationByIndex(line, info.getDestinationIndexes());

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
