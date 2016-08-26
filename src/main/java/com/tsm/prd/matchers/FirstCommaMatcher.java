package com.tsm.prd.matchers;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get Mlini from -> Mlini, Dubrovnik Riviera, Croatia
 */
public class FirstCommaMatcher implements Matcher {
    private static final Splitter SPLITTER = Splitter.on(",").limit(1);
    private static FirstCommaMatcher matcher = null;

    private FirstCommaMatcher() {
    }

    public static FirstCommaMatcher get() {
        if (matcher == null) {
            matcher = new FirstCommaMatcher();
        }
        return matcher;
    }

    public Optional<String> apply(String original) {
        checkNotNull(original, "Original departure is null");
        List<String> result = SPLITTER.splitToList(original);
        if (result.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(result.get(0).toLowerCase().trim());
    }
}
