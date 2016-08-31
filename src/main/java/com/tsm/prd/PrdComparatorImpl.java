package com.tsm.prd;

import com.google.common.base.Optional;
import com.tsm.prd.config.ProviderConfig;
import com.tsm.prd.objects.*;
import com.google.common.base.Splitter;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class PrdComparatorImpl extends DataManager implements PrdComparator {
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

        // input route and output route are matching the PRD mappings for:
        // when multiple resorts needs to be called for a region or vice versa in comparison with PRD.
        Set<Route> multipleResortsRoutes = multipleResorts(partnerRoutes, boRoutes, providerConfig.isAirportGroupSupporting());
        writeInCsv(multipleResortsRoutes, StepMessage.MULTIPLE_RESORTS, providerConfig.getBoHeaders());
    }


    /**
     * Some concerns:
     * we can't match "Nissi Bay, Cyprus East, Cyprus" and "Cyprus East (incl Ayia Napa and Larnaca)"
     */
    protected Set<Route> multipleResorts(Set<Route> partnerRoutes, Set<Route> boRoutes, boolean isAirportGroupSupporting) {
        final Set<Route> resultRoutes = new HashSet<>();
        final Set<Route> routesWithAirportGroupForChecking = new HashSet<>();


        for (final Route boRoute : boRoutes) {
            for (final Route partnerRoute : partnerRoutes) {
                //if a partner doesn't support groups and has only county as destination - invalid route.
                boolean groupNotSupporting = partnerRoute.getOriginDestination().isCountryOnly() && !isAirportGroupSupporting;
                boolean isStrictSameRote = isStrictSameRoute(boRoute, partnerRoute);

                if (groupNotSupporting && isStrictSameRote) {
                    resultRoutes.add(boRoute);
                    continue;
                }

                if (isStrictSameRote && Airports.isAirportGroup(boRoute.getDepartureName())) {
                    routesWithAirportGroupForChecking.add(boRoute);
                }
            }
        }

        for (final Route routeForChecking : routesWithAirportGroupForChecking) {
            for (final OutputRoute outputRoute : routeForChecking.getOutputRoutes()) {

                Optional<Route> boRouteOpt = findRouteByOutputRoute(boRoutes, outputRoute);
                if (!boRouteOpt.isPresent()) {
                    continue;
                }

                final Route boRoute = boRouteOpt.get();
                boolean isSameDeparture = Airports.isSameAirportGroupOrSameAirport(routeForChecking.getDepartureName(), boRoute.getDepartureName());
                boolean isSameDestination = isStrictSameDestination(routeForChecking.getDestinationName(), boRoute.getDestinationName());
                // checking correct or incorrect this route
                if (!isSameDeparture || !isSameDestination) {
                    resultRoutes.add(routeForChecking);
                }
            }
        }

        return resultRoutes;
    }

    protected Optional<Route> findRouteByOutputRoute(Set<Route> boRoutes, OutputRoute outputRoute) {
        for (final Route boRoute : boRoutes) {
            if (outputRoute.getOutputDepartureId().equals(boRoute.getDepartureId())
                    && outputRoute.getOutputDestinationId().equals(boRoute.getDestinationId())) {
                return Optional.of(boRoute);
            }
        }
        return Optional.absent();
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
            if (isNotContainRoute(route.getDepartureName(), route.getDestinationName(), two)) {
                results.add(route);
            }
        }
        return results;
    }

    protected boolean isNotContainRoute(final String departure, String destination, final Set<Route> set) {
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

    protected boolean isStrictSameDestination(String boDestination, String partnerDestination) {
        final List<String> splitTwoDestinations = COMMA_SPLITTER.splitToList(partnerDestination);
        final List<String> splitOneDestinations = COMMA_SPLITTER.splitToList(boDestination);
        return splitOneDestinations.containsAll(splitTwoDestinations);
    }

    protected boolean isSameRoute(final Route routeOne, final Route routeTwo) {
        final String departure = routeTwo.getDepartureName();
        final String destination = routeTwo.getDestinationName();
        final List<String> splitDestinations = COMMA_SPLITTER.splitToList(destination);
        return isSameRoute(routeOne, departure, destination, splitDestinations);
    }

    protected boolean isSameRoute(final Route route, final String departure, final String destination, final List<String> splitDestinations) {
        final List<String> splitComparedDestinations = COMMA_SPLITTER.splitToList(route.getDestinationName());
        return (route.getDepartureName().contains(departure) || departure.contains(route.getDepartureName())) && (
                route.getDestinationName().contains(destination) || destination.contains(route.getDestinationName())) || ((
                splitComparedDestinations.containsAll(splitDestinations) || splitDestinations.containsAll(splitComparedDestinations)));
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
