package com.anapedra.stock_manager.domain.enums;

import java.util.Arrays;

/**
 * Representa o status de um Pedido (Order) no sistema.
 *
 * <p>Cada status possui um código inteiro fixo, garantindo a persistência estável
 * no banco de dados, independentemente da ordem declarada no código ou de futuras alterações.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public enum OrderStatus {

    /*
    Attention: When inserting another enumerator, sequence the integers
    in the proposed order to avoid a possible collapse of subsequent codes.
     */

    /**
     * O pedido foi criado, mas o pagamento ainda está pendente (código 1).
     */
    WAITING_PAYMENT(1),

    /**
     * O pagamento do pedido foi recebido com sucesso (código 2).
     */
    PAID(2),

    /**
     * O pedido foi enviado ao cliente (código 3).
     */
    SHIPPED(3),

    /**
     * O pedido foi entregue ao cliente (código 4).
     */
    DELIVERED(4),

    /**
     * O pedido foi cancelado (código 5).
     */
    CACELED(5);

    private int code;

    /**
     * Construtor do enum.
     * @param code O código inteiro que representa o status.
     */
    private OrderStatus(int code){
        this.code=code;
    }

    /**
     * Retorna o código inteiro do status.
     * @return O código.
     */
    public int getCode(){
        return code;
    }

    /**
     * Converte um código inteiro em seu respectivo {@code OrderStatus}.
     *
     * @param code O código inteiro a ser consultado.
     * @return O {@code OrderStatus} correspondente ao código.
     * @throws IllegalArgumentException Se o código fornecido não for válido.
     */
    public static OrderStatus valueOf(int code){
       for(OrderStatus value : OrderStatus.values() ) {
           if (value.getCode() == code){
               return value;
           }
       }
       throw new IllegalArgumentException("Invalid code!");
    }

}