package com.ozanthongtomi.pizzeria;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NominatimResult {
    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lon")
    private String lon;

    @JsonProperty("display_name")
    private String displayName;

    public Double getLat() {
        return lat == null ? null : Double.valueOf(lat);
    }

    public Double getLon() {
        return lon == null ? null : Double.valueOf(lon);
    }

    public String getDisplayName() {
        return displayName;
    }
}
