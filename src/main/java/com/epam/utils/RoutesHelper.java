package com.epam.utils;

import com.epam.utils.objects.Route;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javafx.util.Pair;

import java.util.List;
import java.util.Set;

public class RoutesHelper {
    /**
     * Deduplicate routes.
     *
     * @param routes routes
     * @return key - original values, value - duplicated values
     */
    public Pair<Set<Route>, List<Route>> deduplicateRoutes(ListMultimap<String, Route> routes) {
        final Set<Route> set = Sets.newHashSet();
        final List<Route> duplicated = Lists.newArrayList();

        for (Route route : routes.values()) {
            boolean duplicate = set.add(route);
            if (!duplicate) {
                duplicated.add(route);
            }
        }

        return new Pair<>(set, duplicated);
    }
}
