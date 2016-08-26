package com.tsm.prd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class ConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);
    private static ConfigUtil configUtil = null;
    private final ObjectMapper mapper = new ObjectMapper();

    private ConfigUtil() {
    }

    public static ConfigUtil getInstance() {
        if (configUtil == null) {
            configUtil = new ConfigUtil();
        }
        return configUtil;
    }

    public AppConfig load() {
        try {
            return mapper.readValue(new File("conf/app.json"), AppConfig.class);
        } catch (IOException e) {
            LOGGER.error("app.json wasn't loaded", e);
        }
        throw new IllegalStateException("something wrong with app config");
    }
}
