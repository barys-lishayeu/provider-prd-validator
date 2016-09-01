package com.tsm.prd.locations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsm.prd.objects.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public final class LocationUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUtil.class);
    private static LocationUtil locationUtil = null;
    private static final ObjectMapper om = new ObjectMapper();
    private static List<Location> locations;

    private LocationUtil() {
    }

    public static LocationUtil get() {
        if (locationUtil == null) {
            locationUtil = new LocationUtil();
        }
        return locationUtil;
    }

    public void loadLocations() {
        try {
            locations = om.readValue(getClass().getResource("/locations.json"), new TypeReference<List<Location>>() {
            });
        } catch (IOException e) {
            LOGGER.error("Can't load locations from file");
            e.printStackTrace();
        }
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Location getLocationById(String id) {
        for (Location location : locations) {
            if (location.get_id().equalsIgnoreCase(id)) {
                return location;
            }
        }
        return null;
    }
}
