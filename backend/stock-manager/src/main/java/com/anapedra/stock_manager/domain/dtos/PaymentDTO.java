package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Payment;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO (Data Transfer Object) para a entidade Pagamento (Payment).
 *
 * <p>Usado para transferir dados sobre a transação de pagamento, incluindo
 * o momento em que ocorreu e o valor total.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class PaymentDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * O identificador único do pagamento.
     */
    public Long id;

    /**
     * O momento em que o pagamento foi efetuado.
     */
    private Instant moment;

    /**
     * O valor total pago. (Geralmente retirado do Total do Pedido).
     */
    private Double totalPayment;

    /**
     * O DTO do pedido associado (usado principalmente em construtores de entrada).
     */
    private OrderDTO order;

    /**
     * Construtor padrão sem argumentos.
     */
    public PaymentDTO() {

    }

    /**
     * Construtor para inicializar o DTO na criação, assumindo que o valor total
     * é retirado do DTO do pedido.
     *
     * @param id O ID do pagamento.
     * @param moment O momento do pagamento.
     * @param order O DTO do pedido associado.
     */
    public PaymentDTO(Long id, Instant moment,OrderDTO order) {
        this.id = id;
        this.moment = moment;
        this.order = order;
        this.totalPayment = order.getTotal();

    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Payment}.
     *
     * @param entity A entidade Payment de origem.
     */
    public PaymentDTO(Payment entity) {
        this.id = entity.getId();
        moment = entity.getMoment();
        // Assume que a referência Order está carregada para buscar o total
        totalPayment=  entity.getOrder().getTotal();

    }

    /**
     * Retorna o momento do pagamento.
     * @return O Instant do pagamento.
     */
    public Instant getMoment() {
        return moment;
    }

    /**
     * Define o momento do pagamento.
     * @param moment O novo Instant.
     */
    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    /**
     * Retorna o ID do pagamento.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID do pagamento.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o valor total pago.
     * @return O valor total.
     */
    public Double getTotalPayment() {
        return totalPayment;
    }
}