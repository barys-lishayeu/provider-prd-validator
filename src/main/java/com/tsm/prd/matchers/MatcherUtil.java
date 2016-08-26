package com.tsm.prd.matchers;

import com.google.common.base.Optional;

public class MatcherUtil {

    public static Optional<String> apply(String original, Matcher... matchers) {
        for (Matcher matcher : matchers) {
            Optional<String> result = matcher.apply(original);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.absent();
    }
}
