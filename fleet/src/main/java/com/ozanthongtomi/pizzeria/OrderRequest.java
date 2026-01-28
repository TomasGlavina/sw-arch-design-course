package com.ozanthongtomi.pizzeria;

public class OrderRequest {
    private String address;
    private int weight;

    public OrderRequest() {}

    public OrderRequest(String address, int weight) {
        this.address = address;
        this.weight = weight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
