package com.anapedra.stock_manager.domain.enums;

public enum OrderStatus {

    /*
    Attention: When inserting another enumerator, sequence the integers
    in the proposed order to avoid a possible collapse of subsequent codes.
     */

    WAITING_PAYMENT(1),
    PAID(2),
    SHIPPED(3),
    DELIVERED(4),
    CACELED(5);

    private int code;

    private OrderStatus(int code){
        this.code=code;
    }
    public int getCode(){
        return code;
    }
    public static OrderStatus valueOf(int code){
       for(OrderStatus value : OrderStatus.values() ) {
           if (value.getCode() == code){
               return value;
           }
       }
       throw new IllegalArgumentException("Invalid code!");
    }

}
