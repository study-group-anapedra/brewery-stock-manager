package com.anapedra.stock_manager.domain.enums;

/**
 * Representa o estado do estoque de um produto (Beer) no sistema.
 *
 * <p>Define se a quantidade em estoque é {@code AVAILABLE} (disponível),
 * {@code LOW} (baixa) ou {@code OUT_OF_STOCK} (esgotada).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public enum StockStatus {

    /**
     * Estoque suficiente e acima do limite mínimo (código 1).
     */
    AVAILABLE(1),   // Enough stock

    /**
     * Estoque baixo, abaixo do limite mínimo definido na classe {@code Stock} (código 2).
     */
    LOW(2),         // Below the minimum threshold

    /**
     * Estoque esgotado, com zero unidades disponíveis (código 3).
     */
    OUT_OF_STOCK(3); // No units available

    private final int code;

    /**
     * Construtor do enum.
     * @param code O código inteiro que representa o status do estoque.
     */
    StockStatus(int code) {
        this.code = code;
    }

    /**
     * Retorna o código inteiro do status.
     * @return O código.
     */
    public int getCode() {
        return code;
    }

    /**
     * Converte um código inteiro em seu respectivo {@code StockStatus}.
     *
     * @param code O código inteiro a ser consultado.
     * @return O {@code StockStatus} correspondente ao código.
     * @throws IllegalArgumentException Se o código fornecido não for válido.
     */
    public static StockStatus valueOf(int code) {
        for (StockStatus value : StockStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid code for StockStatus: " + code);
    }
}