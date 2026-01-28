package com.ozanthongtomi.pizzeria;

public class OrderException extends RuntimeException {
    private final int status;

    public OrderException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
