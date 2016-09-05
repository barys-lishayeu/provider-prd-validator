package com.tsm.prd;

import com.tsm.prd.config.ProviderConfig;
import com.tsm.prd.locations.LocationUtil;
import com.tsm.prd.matchers.AllAirportsMatcher;
import com.tsm.prd.matchers.CodeMatcher;
import com.tsm.prd.matchers.MatcherUtil;
import com.tsm.prd.matchers.UnderscoreMatcher;
import com.tsm.prd.objects.*;
import com.google.common.base.Splitter;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class PrdComparatorImpl extends DataManager implements PrdComparator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrdComparatorImpl.class);
    public static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults();
    private RoutesHelper routesHelper = new RoutesHelper();
    private MappingsHelper mappingsHelper = new MappingsHelper();
    private ProviderConfig providerConfig;
    private String rootPath;

    /**
     * compare BO routes and partner routes
     *
     * @param providerConfig providerConfig
     */
    @Override
    public void run(ProviderConfig providerConfig, String rootPath) {
        this.providerConfig = providerConfig;
        this.rootPath = rootPath;
        ListMultimap<String, Route> boDirtyRoutes = loadBoRoutes(providerConfig);
        ListMultimap<String, Route> partnerDirtyRoutes = loadPartnerRoutes(providerConfig);
        ListMultimap<String, Mappings> mappings = loadMappings(providerConfig);

        //deduplicate
        Pair<Set<Route>, List<Route>> deDuplicatedBoRoutes = routesHelper.deduplicateRoutes(boDirtyRoutes);
        writeInCsv(deDuplicatedBoRoutes.getValue(), StepMessage.DUPLICATE_ROUTES, providerConfig.getBoHeaders());
        Set<Route> boRoutes = deDuplicatedBoRoutes.getKey();
        Pair<Set<Route>, List<Route>> deDuplicatedPartnerRoutes = routesHelper.deduplicateRoutes(partnerDirtyRoutes);
        Set<Route> partnerRoutes = deDuplicatedPartnerRoutes.getKey();

        //compare partner PRD with BO PRD
        writeInCsv(compare(partnerRoutes, boRoutes), StepMessage.IN_PRD_BUT_NOT_IN_BO, providerConfig.getPartnerHeaders());

        //compare BO PRD with partner PRD
        writeInCsv(compare(boRoutes, partnerRoutes), StepMessage.IN_BO_BUT_NOT_IN_PRD, providerConfig.getBoHeaders());

        //deduplicate mappings
        Pair<Set<Mappings>, List<Mappings>> deDuplicatedMappings = mappingsHelper.deduplicateMappings(mappings);
        writeInCsv(deDuplicatedMappings.getValue(), StepMessage.DUPLICATE_MAPPINGS, providerConfig.getMappingsHeaders());

        // all routes that serve single airport also appear in the airport group setup
        final Set<Route> routesNotGrouped = new HashSet<>();
        for (Route route : partnerRoutes) {
            final List<String> group = Airports.getAirportsGroupsByChild(route.getDepartureName());
            if (!group.isEmpty()) {
                for (String airportFromGroup : group) {
                    if (isNotContainRoute(airportFromGroup.toLowerCase(), route.getDestinationName(), boRoutes)) {
                        routesNotGrouped.add(route);
                    }
                }
            }
        }
        writeInCsv(routesNotGrouped, StepMessage.NOT_IN_THE_AIRPORT_GROUP, providerConfig.getPartnerHeaders());

        // check output routes for groups
        Pair<Set<Route>, Set<Route>> invalidRoutesInGroup = checkingOutputRoutesInGroups(partnerRoutes, boRoutes, providerConfig.isAirportGroupSupporting());
        writeInCsv(invalidRoutesInGroup.getKey(), StepMessage.WRONG_GROUP_FOR_OUTPUT_ROUTE, providerConfig.getBoHeaders());
        writeInCsv(invalidRoutesInGroup.getValue(), StepMessage.INVALID_OUT_ROUTES_IN_GROUP, providerConfig.getBoHeaders());
    }

    /**
     *  point 6 part 1
         input route and output route are matching the PRD mappings for:
         when multiple resorts needs to be called for a region or vice versa in comparison with PRD.
     * Some concerns:
     * we can't match "Nissi Bay, Cyprus East, Cyprus" and "Cyprus East (incl Ayia Napa and Larnaca)"
     */
    protected Pair<Set<Route>, Set<Route>> checkingOutputRoutesInGroups(Set<Route> partnerRoutes, Set<Route> boRoutes, boolean isAirportGroupSupporting) {
        final Set<Route> resultsPart1 = new HashSet<>();
        final Set<Route> routesWithAirportGroupForChecking = new HashSet<>();
        final Set<Route> resultsPart2 = new HashSet<>();

        for (final Route boRoute : boRoutes) {
            for (final Route partnerRoute : partnerRoutes) {
                //if a partner doesn't support groups and has only county as destination - invalid route.
                boolean groupNotSupporting = partnerRoute.getOriginDestination().isCountryOnly() && !isAirportGroupSupporting;
                boolean isStrictSameRote = isStrictSameRoute(boRoute, partnerRoute);

                if (isStrictSameRote && groupNotSupporting) {
                    resultsPart1.add(boRoute);
                    continue;
                }

                if (Airports.isAirportGroup(boRoute.getDepartureName())) {
                    routesWithAirportGroupForChecking.add(boRoute);
                }
            }
        }

        for (final Route routeForChecking : routesWithAirportGroupForChecking) {
            for (final OutputRoute outputRoute : routeForChecking.getOutputRoutes()) {

                final Pair<Location, Location> locationPair = findLocationsByOutputRoute(outputRoute);
                final Location departure = locationPair.getKey();
                final Location destination = locationPair.getValue();

                if(departure == null || destination == null) {
                    continue;
                }

                final String departureName = MatcherUtil.apply(departure.getFullName_En(), CodeMatcher.get(), AllAirportsMatcher.get(), UnderscoreMatcher.get()).get();
                final String destinationName = MatcherUtil.apply(destination.getFullName_En(), UnderscoreMatcher.get()).get();

                // point 6 part 2
                // if input route LON-Barbados and in PRD file we can see that there is supporting only LGW from group LON
                // it means in output routes should be only LGW route.
                {
                    if (!isAvailablePartnerRouteByLocations(departureName, destinationName, partnerRoutes)) {
                        resultsPart2.add(routeForChecking);
                    }
                }

                // to validate that the output for the group is correct
                boolean isSameDeparture = Airports.isSameAirportGroupOrSameAirport(routeForChecking.getDepartureName(), departureName);
                if (!isSameDeparture) {
                    resultsPart1.add(routeForChecking);
                }
            }
        }

        return new Pair<>(resultsPart1, resultsPart2);
    }

    protected boolean isAvailablePartnerRouteByLocations(String departureName, String destinationName, Set<Route> partnerRoutes) {
        for (Route partnerRoute : partnerRoutes) {
            if (isSameRoute(departureName, destinationName, partnerRoute.getDepartureName(), partnerRoute.getDestinationName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Key - departure
     * Value - destination
     */
    protected Pair<Location, Location> findLocationsByOutputRoute(OutputRoute outputRoute) {
        Location departure = LocationUtil.get().getLocationById(outputRoute.getOutputDepartureId());
        Location destination = LocationUtil.get().getLocationById(outputRoute.getOutputDestinationId());
        return new Pair<>(departure, destination);
    }

    /**
     * Compare 2 sets
     *
     * @param first first
     * @param two   two
     * @return differences
     */
    protected Iterable<Route> compare(final Set<Route> first, final Set<Route> two) {
        checkNotNull(first, "first");
        checkNotNull(two, "two");

        final Set<Route> results = Sets.newHashSet();

        // check if first is not in two
        for (Route route : first) {
            if (isNotContainRoute(route, two)) {
                results.add(route);
            }
        }
        return results;
    }

    protected boolean isNotContainRoute(final String departure, final String destination, final Set<Route> set) {
        boolean contain = false;
        final List<String> splitDestinations = COMMA_SPLITTER.splitToList(destination);

        for (Route routeTwo : set) {
            if (isSameRoute(routeTwo, departure, destination, splitDestinations)) {
                contain = true;
                break;
            }
        }
        return !contain;
    }

    protected boolean isNotContainRoute(final Route route, final Set<Route> set) {
        boolean contain = false;
        final List<String> splitDestinations = COMMA_SPLITTER.splitToList(route.getDestinationName());

        OriginDestination originDestination = route.getOriginDestination();
//        if(originDestination != null && providerConfig.isAirportGroupSupporting()) {
//
//        }

        for (Route routeTwo : set) {

//            if(route.getOriginal()[0].equalsIgnoreCase("571df77ae4b0dcfea3c5d859") && routeTwo.getDestinationName().contains("gran canaria")) {
//                System.out.println("");
//            }

            if (isSameRoute(routeTwo, route.getDepartureName(), route.getDestinationName(), splitDestinations)) {
                contain = true;
                break;
            }
        }
        return !contain;
    }

    /**
     * Example: boRoute-> 'gla: mahon, menorca, balearic islands, spain'
     * partnerRoute-> 'gla: mahon, menorca, spain'
     * as a result it is the same routes
     *
     * @param boRoute      boRoute
     * @param partnerRoute partnerRoute
     * @return true if the same
     */
    protected boolean isStrictSameRoute(final Route boRoute, final Route partnerRoute) {
        final String departure = partnerRoute.getDepartureName();
        final String destination = partnerRoute.getDestinationName();
        final List<String> splitTwoDestinations = COMMA_SPLITTER.splitToList(destination);
        final List<String> splitOneDestinations = COMMA_SPLITTER.splitToList(boRoute.getDestinationName());
        if (partnerRoute.getOriginDestination() != null && partnerRoute.getOriginDestination().isCountryOnly()) {
            return boRoute.getDepartureName().equals(departure) && boRoute.getDestinationName().equals(destination);
        }
        return boRoute.getDepartureName().equals(departure) && splitOneDestinations.containsAll(splitTwoDestinations);
    }

    protected boolean isSameRoute(final Route route, final String departure, final String destination, final List<String> splitDestinations) {
        final List<String> splitComparedDestinations = COMMA_SPLITTER.splitToList(route.getDestinationName());
        return (route.getDepartureName().contains(departure) || departure.contains(route.getDepartureName())) && ((
                route.getDestinationName().contains(destination) || destination.contains(route.getDestinationName())) || ((
                splitComparedDestinations.containsAll(splitDestinations) || splitDestinations.containsAll(splitComparedDestinations))));
    }

    protected boolean isSameRoute(final String departureBo, final String destinationBo, final String departurePartner, final String destinationPartner) {
        final List<String> partnerDestinations = COMMA_SPLITTER.splitToList(destinationPartner);
        final List<String> boDestinations = COMMA_SPLITTER.splitToList(destinationBo);
        return (departureBo.contains(departurePartner) || departurePartner.contains(departureBo)) && ((
                destinationBo.contains(destinationPartner) || destinationPartner.contains(destinationBo)) || ((
                boDestinations.containsAll(partnerDestinations))));
    }

    @Override
    public String getProviderName() {
        return providerConfig.getProviderName();
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }
}
