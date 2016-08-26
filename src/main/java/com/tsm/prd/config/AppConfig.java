package com.tsm.prd.config;

import java.util.List;

public class AppConfig {
    private String rootPath;
    private List<ProviderConfig> providerConfigs;

    public String getRootPath() {
        return rootPath;
    }

    public List<ProviderConfig> getProviderConfigs() {
        return providerConfigs;
    }
}
