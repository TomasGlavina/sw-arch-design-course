package com.ozanthongtomi.drones.model;

import java.util.Objects;

public class NewDroneRequest {

    private Long id;
    private String name;
    private int capacity;
    private String status;

    public NewDroneRequest() {}

    public NewDroneRequest(Long id, String name, int capacity, String status){
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NewDroneRequest{id=" + id + ", name='" + name + "', capacity=" + capacity + ", status='" + status + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NewDroneRequest other = (NewDroneRequest) obj;
        return capacity == other.capacity
            && java.util.Objects.equals(id, other.id)
            && java.util.Objects.equals(name, other.name)
            && java.util.Objects.equals(status, other.status);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, capacity, status);
    }
}
