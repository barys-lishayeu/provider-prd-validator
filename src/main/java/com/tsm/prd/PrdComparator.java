package com.tsm.prd;

import com.tsm.prd.config.ProviderConfig;

public interface PrdComparator {
    void run(ProviderConfig providerConfig, String rootPath);
}
