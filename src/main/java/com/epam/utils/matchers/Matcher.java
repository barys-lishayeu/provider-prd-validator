package com.epam.utils.matchers;

import com.google.common.base.Optional;

public interface Matcher {
    Optional<String> apply(String original);
}
