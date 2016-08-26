package com.epam.utils.holidays;

import au.com.bytecode.opencsv.CSVReader;
import com.epam.utils.PrdComparator;
import com.epam.utils.objects.FileInfoPrdPartner;
import com.epam.utils.objects.header.HeaderIndex;
import com.epam.utils.objects.Route;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TeletextPrdComparator extends PrdComparator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeletextPrdComparator.class);

    @Override
    public String getProviderName() {
        return "teletext";
    }

    @Override
    public ListMultimap<String, Route> loadPartnerRoutes(final FileInfoPrdPartner info) {
        final ListMultimap<String, Route> routes = ArrayListMultimap.create();
        final HeaderIndex headerIndex = info.getPartnerIndexes();

        try (final CSVReader importReader = new CSVReader(new BufferedReader(new FileReader(new File(providerPath() + info.getPartnerFileName()))))) {
            info.setPartnerHeaders(importReader.readNext());

            for (String[] line = importReader.readNext(); line != null; line = importReader.readNext()) {
                final String departure = line[headerIndex.getDepIndex()];
                final String destination = getDestinationByIndex(line, headerIndex);
                Route route = new Route(departure, destination, line);
                cleanPartnerRoute(route);
                routes.put(line[headerIndex.getDepIndex()], route);
            }

        } catch (IOException e) {
            LOGGER.error("Can't load the file {}", info.getPartnerFileName());
        }
        return routes;
    }

}
