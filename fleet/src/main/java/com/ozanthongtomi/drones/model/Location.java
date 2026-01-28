package com.ozanthongtomi.drones.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
@Embeddable
public class Location {
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;

    public Location() {}

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
