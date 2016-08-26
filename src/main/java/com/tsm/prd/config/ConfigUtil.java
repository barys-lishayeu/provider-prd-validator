package com.tsm.prd.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);
    private static ConfigUtil configUtil = null;
    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode jsonNode;

    private ConfigUtil() {
    }

    public static ConfigUtil getInstance() {
        if (configUtil == null) {
            configUtil = new ConfigUtil();
        }
        return configUtil;
    }

    public void reload() {
        try {
            jsonNode = mapper.readTree(new File("conf/app.json"));
            LOGGER.debug("app.json: {}", jsonNode.toString());
        } catch (IOException e) {
            LOGGER.error("app.json wasn't loaded", e);
        }
    }

    public String get() {
        checkNotNull(jsonNode, "We need to reload app.json");


        return "";
    }

}
