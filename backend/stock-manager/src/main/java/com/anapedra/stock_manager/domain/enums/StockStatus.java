package com.anapedra.stock_manager.domain.enums;

public enum StockStatus {

    AVAILABLE(1),   // Enough stock
    LOW(2),         // Below the minimum threshold
    OUT_OF_STOCK(3); // No units available

    private final int code;

    StockStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StockStatus valueOf(int code) {
        for (StockStatus value : StockStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid code for StockStatus: " + code);
    }
}