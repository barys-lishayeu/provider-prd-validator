package com.tsm.prd.objects;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    private static final String EN = "en";
    private static final String EN_GB = "en_GB";
    @Id
    @ObjectId
    private String _id;
    private Map<String, String> fullName = new HashMap<>();
    private String iataCode;
    private String locationGroup;
    private String type;
    @ObjectId
    private String touristParentId;
    private Map<String, String> autoComplete = new HashMap<>();

    public boolean isDeparture() {
        return autoComplete.get("holidaysDeparture") != null;
    }

    public boolean isDestination() {
        return autoComplete.get("holidaysDestination") != null;
    }

    public Map<String, String> getFullName() {
        return fullName;
    }

    public String getIataCode() {
        return iataCode;
    }

    public String getLocationGroup() {
        return locationGroup;
    }

    public String getType() {
        return type;
    }

    public String getTouristParentId() {
        return touristParentId;
    }

    public Map<String, String> getAutoComplete() {
        return autoComplete;
    }

    /**
     * Gets fullName for EN locale.
     */
    @JsonIgnore
    public String getFullName_En() {
        return fullName.get(EN);
    }

    /**
     * Sets fullname for EN locale.
     */
    @JsonIgnore
    public void setFullName_En(String nameEn) {
        fullName.put(EN, nameEn);
    }


    public String get_id() {
        return _id;
    }
}
