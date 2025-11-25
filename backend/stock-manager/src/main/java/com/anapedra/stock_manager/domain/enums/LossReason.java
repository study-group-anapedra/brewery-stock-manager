package com.anapedra.stock_manager.domain.enums;

public enum LossReason {
    DAMAGED(1),      // Danificado (Robo, quebra, etc.)
    EXPIRED(2),      // Vencimento
    THEFT(3),        // Roubo / Furto
    OTHER(4);        // Outros motivos

    private final int code;

    LossReason(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static LossReason valueOf(int code) {
        for (LossReason value : LossReason.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid code for LossReason: " + code);
    }
}