package com.ozanthongtomi.drones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Table(name = "dronoraDB")
@Entity
public class Drone {

    @Id
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private int capacity;

    @NotNull
    private String status;
    // status can be either AVAILABLE or RESERVED

    public Drone() {}

    public Drone(Long id, String name, int capacity, String status) {
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
        return "Drone{id=" + id + ", name='" + name + "', capacity=" + capacity + ", status='" + status + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Drone other = (Drone) obj;
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
