package com.anapedra.stock_manager.domain.enums;

public enum OrderStatus {

    WAITING_PAYMENT(1), // Esperando Pagamento (Se o Payment for null)
    PAID(2),            // Pago (Se o Payment for não-nulo)
    SHIPPED(3),         // Enviado
    DELIVERED(4),       // Entregue
    CANCELED(5);        // Cancelado

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    public static OrderStatus valueOf(int code) {
        for (OrderStatus value : OrderStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus code: " + code);
    }
}