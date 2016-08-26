package com.epam.utils.matchers;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get South East from -> South East - All Airports
 */
public class AllAirportsMatcher implements Matcher {
    private static AllAirportsMatcher matcher = null;

    private AllAirportsMatcher() {
    }

    public static AllAirportsMatcher get() {
        if(matcher == null) {
            matcher = new AllAirportsMatcher();
        }
        return matcher;
    }

    public Optional<String> apply(String original) {
        checkNotNull(original, "Original departure is null");
        return Optional.of(original.toLowerCase().replace("-", "").replace("all airports", "").trim());
    }
}
