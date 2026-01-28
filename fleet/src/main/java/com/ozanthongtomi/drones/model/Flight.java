package com.ozanthongtomi.drones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Table(name="dronoraDBflights")
@Entity
public class Flight {
    @Id
    @NotNull
    private Long id;

    @NotNull
    private int weight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "starting_point_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "starting_point_longitude"))
    })
    @NotNull
    private Location startingPoint;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude"))
    })
    @NotNull
    private Location destination;

    @Column(name = "drone_id")
    @NotNull
    private Long droneId;

    @NotNull
    private String status;

    public Flight() {}

    public Flight(Long id, int weight, Location startingPoint, Location destination, Long droneId, String status) {
        this.id = id;
        this.weight = weight;
        this.startingPoint = startingPoint;
        this.destination = destination;
        this.droneId = droneId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Location getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Location startingPoint) {
        this.startingPoint = startingPoint;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Long getDroneId() {
        return droneId;
    }

    public void setDroneId(Long droneId) {
        this.droneId = droneId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Flight{id=" + id + ", weight=" + weight + ", startingPoint=" + startingPoint
            + ", destination=" + destination + ", droneId=" + droneId + ", status='" + status + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Flight other = (Flight) obj;
        return weight == other.weight
            && java.util.Objects.equals(id, other.id)
            && java.util.Objects.equals(startingPoint, other.startingPoint)
            && java.util.Objects.equals(destination, other.destination)
            && java.util.Objects.equals(droneId, other.droneId)
            && java.util.Objects.equals(status, other.status);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, weight, startingPoint, destination, droneId, status);
    }
}
