package com.tsm.prd;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.tsm.prd.matchers.AllAirportsMatcher;
import com.tsm.prd.matchers.CodeMatcher;
import com.tsm.prd.matchers.FirstCommaMatcher;
import com.tsm.prd.matchers.MatcherUtil;
import com.tsm.prd.matchers.UnderscoreMatcher;
import com.tsm.prd.objects.*;
import com.tsm.prd.objects.ConfigBo;
import com.tsm.prd.objects.header.BoHeader;
import com.tsm.prd.objects.header.BoHeaderHolder;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import static com.google.common.base.Preconditions.checkElementIndex;

/**
 * Manage loading and preparing data from CSV files
 */
public abstract class DataManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);
    private static final Joiner JOINER = Joiner.on(", ").skipNulls();
    private static final List<String> MAPPINGS_INPUT_KEYS = Lists.newArrayList("Destination", "Departure");
    public static final String DELIMETER = "_";
    public static final String EXT = ".csv";

    public abstract ListMultimap<String, Route> loadPartnerRoutes(final ConfigPrdPartner info);

    /**
     * @param info ConfigBo
     * @return key - code like LON, LGW
     */
    protected ListMultimap<String, Route> loadBoRoutes(ConfigBo info) {
        final ListMultimap<String, Route> routes = ArrayListMultimap.create();

        try (final CSVReader iReader = new CSVReader(new BufferedReader(new FileReader(new File(providerPath() + info.getBoFileName()))))) {
            final String[] headers = iReader.readNext();
            info.setBoHeaders(headers);
            BoHeaderHolder.analysis(headers);

            int isActiveIndex = BoHeaderHolder.getIndex(BoHeader.IS_ACTIVE);

            for (String[] line = iReader.readNext(); line != null; line = iReader.readNext()) {
                checkElementIndex(isActiveIndex, line.length);
                final String isActive = line[isActiveIndex];

                if (Boolean.TRUE.toString().equalsIgnoreCase(isActive.trim())) {
                    final String departureId = line[BoHeaderHolder.getIndex(BoHeader.INPUT_DEPARTURE_ID)];
                    final String departureName = line[BoHeaderHolder.getIndex(BoHeader.INPUT_DEPARTURE_NAME)];
                    final String destinationId = line[BoHeaderHolder.getIndex(BoHeader.INPUT_DESTINATION_ID)];
                    final String destinationName = line[BoHeaderHolder.getIndex(BoHeader.INPUT_DESTINATION_NAME)];
                    final List<OutputRoute> outputRoutes = getAllOutputRoutes(line);

                    final Route route = new Route(departureName, departureId, destinationName, destinationId, line, outputRoutes);
                    cleanBoRoute(route);
                    routes.put(departureName, route);
                }
            }

        } catch (IOException e) {
            LOGGER.error("Can't load the file {}", info.getBoFileName());
        }
        return routes;
    }

    private List<OutputRoute> getAllOutputRoutes(final String[] line) {
        final Integer priorityFirstIndex = BoHeaderHolder.getIndex(BoHeader.OUTPUT_ROUTES_PRIORITY);

        final List<OutputRoute> outputRoutes = Lists.newArrayList();

        List<String> outputRouteArray = Lists.newArrayList(line).subList(priorityFirstIndex, line.length - 1);
        for (List<String> output : Lists.partition(outputRouteArray, 3)) {
            if (output.size() != 3 || StringUtils.isEmpty(output.get(1)) || StringUtils.isEmpty(output.get(2))) {
                break;
            }
            outputRoutes.add(new OutputRoute(output.get(1), output.get(2)));
        }
        return outputRoutes;
    }

    protected ListMultimap<String, Mappings> loadMappings(ConfigMappings info) {
        final ListMultimap<String, Mappings> mappings = ArrayListMultimap.create();

        try (final CSVReader iReader = new CSVReader(new BufferedReader(new FileReader(new File(providerPath() + info.getMappingsFileName()))))) {
            info.setMappingsHeaders(iReader.readNext());

            for (String[] line = iReader.readNext(); line != null; line = iReader.readNext()) {
                if (MAPPINGS_INPUT_KEYS.contains(line[1])) {
                    mappings.put(line[2], new Mappings(line));//here key = inputValue
                }
            }

        } catch (IOException e) {
            LOGGER.error("Can't load the file {}", info.getMappingsFileName());
        }
        return mappings;
    }

    protected String getDestinationByIndex(final String[] strings, int[] headerIndex) {
        String result = null;
        for (int index : headerIndex) {
            checkElementIndex(index, strings.length);
            final String value = strings[index];
            if (!Strings.isNullOrEmpty(value)) {
                result = JOINER.join(value, result);
            }
        }
        return result;
    }

    /**
     * Writer
     *
     * @param array       array
     * @param stepMessage stepMessage
     */
    protected void writeInCsv(Iterable<? extends AsArray> array, StepMessage stepMessage, String[] headers) {
        final File providerDir = new File(providerPath());
        if (!providerDir.exists()) {
            providerDir.mkdir();
        }

        final String path = providerPath() + getProviderName() + DELIMETER + stepMessage.toString().toLowerCase() + EXT;
        final File exportFile = new File(path);

        try (final CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile))))) {
            csvWriter.writeNext(headers);
            for (AsArray arr : array) {
                csvWriter.writeNext(arr.asArray());
            }
            csvWriter.flush();
        } catch (IOException e) {
            LOGGER.error("Can't write the file {}", path);
            return;
        }
        LOGGER.info("File {} has been written.", path);
    }

    protected String providerPath() {
        return getRootPath() + File.separator + getProviderName() + File.separator;
    }

    protected void cleanBoRoute(Route route) {
        // clean departure
        Optional<String> newDeparture = MatcherUtil.apply(route.getDepartureName(), CodeMatcher.get(), AllAirportsMatcher.get());
        route.setDepartureName(newDeparture.or(route.getDepartureName()));
        // clean destination
        Optional<String> newDestination = MatcherUtil.apply(route.getDestinationName(), FirstCommaMatcher.get());
        route.setDestinationName(newDestination.or(route.getDestinationName()));
    }

    protected void cleanPartnerRoute(Route route) {
        // clean departure
        Optional<String> newDeparture = MatcherUtil.apply(route.getDepartureName(), UnderscoreMatcher.get());
        route.setDepartureName(newDeparture.or(route.getDepartureName()));
        // clean destination
        route.setDestinationName(route.getDestinationName().toLowerCase().trim());
    }

    public abstract String getProviderName();
    public abstract String getRootPath();
}
