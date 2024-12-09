package com.example.printmatic.enums;

public enum OrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELED, // when the costumer cancel an order
    REJECTED, // when the copy center rejects an order
    REFUNDED
}
