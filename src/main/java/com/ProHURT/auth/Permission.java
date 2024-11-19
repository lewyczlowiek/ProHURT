package com.ProHURT.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    // Admin permissions
    USER_MANAGE("user:manage"),
    ITEM_MANAGE("item:manage"),
    PURCHASE_ORDER_CREATE("purchase_order:create"),

    // Store Manager permissions
    INVENTORY_ADD("inventory:add"),
    INVENTORY_MONITOR("inventory:monitor"),
    PURCHASE_ORDER_GENERATE("purchase_order:generate"),

    // Store Staff permissions
    INVENTORY_VIEW("inventory:view"),
    INVENTORY_REQUEST("inventory:request"),
    INVENTORY_UPDATE("inventory:update"),

    // Store Staff permissions for item status update
    ITEM_STATUS_UPDATE("item:status_update");

    private final String permission;
}