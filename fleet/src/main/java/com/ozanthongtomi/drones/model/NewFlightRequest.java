package com.ozanthongtomi.drones.model;

public class NewFlightRequest {
    private Location destination;
    private Location startingPoint;
    private int weight;

    public NewFlightRequest() {}

    public NewFlightRequest(int weight, Location destination, Location startingPoint){
        this.weight = weight;
        this.destination = destination;
        this.startingPoint = startingPoint;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Location getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Location startingPoint) {
        this.startingPoint = startingPoint;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "NewFlightRequest{destination=" + destination + ", startingPoint=" + startingPoint + ", weight=" + weight + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NewFlightRequest other = (NewFlightRequest) obj;
        return weight == other.weight
            && java.util.Objects.equals(destination, other.destination)
            && java.util.Objects.equals(startingPoint, other.startingPoint);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(destination, startingPoint, weight);
    }
}
