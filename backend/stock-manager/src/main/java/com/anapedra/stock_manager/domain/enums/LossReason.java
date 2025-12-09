package com.anapedra.stock_manager.domain.enums;

/**
 * Define os motivos possíveis para a perda de estoque (StockLoss) de um produto.
 *
 * <p>Cada motivo é mapeado para um código inteiro fixo, garantindo a
 * integridade dos dados no banco de dados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public enum LossReason {
    /**
     * Produto danificado (ex: quebra, manuseio inadequado) (código 1).
     */
    DAMAGED(1),      // Danificado (Robo, quebra, etc.)

    /**
     * Produto atingiu a data de validade e precisa ser descartado (código 2).
     */
    EXPIRED(2),      // Vencimento

    /**
     * Perda devido a furto ou roubo (código 3).
     */
    THEFT(3),        // Roubo / Furto

    /**
     * Perda devido a um motivo não listado nas opções anteriores (código 4).
     */
    OTHER(4);        // Outros motivos

    private final int code;

    /**
     * Construtor do enum.
     * @param code O código inteiro que representa o motivo da perda.
     */
    LossReason(int code) {
        this.code = code;
    }

    /**
     * Retorna o código inteiro do motivo da perda.
     * @return O código.
     */
    public int getCode() {
        return code;
    }

    /**
     * Converte um código inteiro em seu respectivo {@code LossReason}.
     *
     * @param code O código inteiro a ser consultado.
     * @return O {@code LossReason} correspondente ao código.
     * @throws IllegalArgumentException Se o código fornecido não for válido.
     */
    public static LossReason valueOf(int code) {
        for (LossReason value : LossReason.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid code for LossReason: " + code);
    }
}