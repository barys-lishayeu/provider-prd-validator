package com.tsm.prd.matchers;

import com.google.common.base.Optional;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get STN from -> London Stansted Airport (STN), Essex, England, United Kingdom
 */
public class CodeMatcher implements Matcher {
    private static final Pattern pattern = Pattern.compile("([A-Z][A-Z][A-Z])");
    private static CodeMatcher matcher = null;

    private CodeMatcher() {
    }

    public static CodeMatcher get() {
        if (matcher == null) {
            matcher = new CodeMatcher();
        }
        return matcher;
    }

    public Optional<String> apply(String original) {
        checkNotNull(original, "Original departure is null");
        java.util.regex.Matcher matcher = pattern.matcher(original);
        if (matcher.find()) {
            return Optional.of(matcher.group(1).toLowerCase().trim());
        }
        return Optional.absent();
    }
}
