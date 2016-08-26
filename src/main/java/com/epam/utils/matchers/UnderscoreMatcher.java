package com.epam.utils.matchers;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get north west from -> NORTH_WEST
 */
public class UnderscoreMatcher implements Matcher {
    private static UnderscoreMatcher matcher = null;

    private UnderscoreMatcher() {
    }

    public static UnderscoreMatcher get() {
        if (matcher == null) {
            matcher = new UnderscoreMatcher();
        }
        return matcher;
    }

    public Optional<String> apply(String original) {
        checkNotNull(original, "Original departure is null");
        return Optional.of(original.replace("_", " ").toLowerCase().trim());
    }
}
