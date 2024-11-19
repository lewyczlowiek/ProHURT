package com.ProHURT.entities;

public enum OrderStatus {
    IN_PROGRESS("W trakcie realizacji"),
    IN_DELIVERY("W dostawie"),
    ACCEPTED("Przyjęte"),
    CANCELED("Anulowane");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}