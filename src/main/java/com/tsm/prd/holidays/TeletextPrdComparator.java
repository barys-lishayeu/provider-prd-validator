package com.tsm.prd.holidays;

import au.com.bytecode.opencsv.CSVReader;
import com.tsm.prd.PrdComparatorImpl;
import com.tsm.prd.objects.ConfigPrdPartner;
import com.tsm.prd.objects.Route;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TeletextPrdComparator extends PrdComparatorImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeletextPrdComparator.class);

    @Override
    public ListMultimap<String, Route> loadPartnerRoutes(final ConfigPrdPartner info) {
        final ListMultimap<String, Route> routes = ArrayListMultimap.create();

        try (final CSVReader importReader = new CSVReader(new BufferedReader(new FileReader(new File(providerPath() + info.getPartnerFileName()))))) {
            info.setPartnerHeaders(importReader.readNext());

            for (String[] line = importReader.readNext(); line != null; line = importReader.readNext()) {
                final String departure = line[info.getDepartureIndex()];
                final String destination = getDestinationByIndex(line, info.getDestinationIndexes());
                Route route = new Route(departure, destination, line);
                cleanPartnerRoute(route);
                routes.put(line[info.getDepartureIndex()], route);
            }

        } catch (IOException e) {
            LOGGER.error("Can't load the file {}", info.getPartnerFileName());
        }
        return routes;
    }

}
