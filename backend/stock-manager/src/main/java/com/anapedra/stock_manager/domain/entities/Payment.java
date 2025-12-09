package com.anapedra.stock_manager.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Representa a entidade Pagamento (Payment).
 * Esta classe mapeia a tabela "tb_payment" no banco de dados.
 *
 * <p>Armazena informações sobre o momento em que um pedido foi pago.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_payment")
public class Payment implements Serializable {
    private static final long serialVersionUID=1L;


    /**
     * O identificador único do pagamento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O momento exato (timestamp) em que o pagamento foi registrado.
     */
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "GMT")
    private Instant moment;


    /**
     * O pedido (Order) ao qual este pagamento está associado.
     * Mapeia o relacionamento One-to-One e contém a chave estrangeira (`order_id`).
     */
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Construtor para inicializar o objeto Payment.
     *
     * @param id O identificador do pagamento.
     * @param moment O momento em que o pagamento foi realizado.
     * @param order O pedido associado.
     */
    public Payment(Long id, Instant moment, Order order) {
       this.id = id;
       this.moment = moment;
       this.order = order;

    }

    /**
     * Construtor padrão sem argumentos.
     */
    public Payment() {

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
     * Retorna o momento em que o pagamento foi realizado.
     * @return O Instant do momento.
     */
    public Instant getMoment() {
       return moment;
    }

    /**
     * Define o momento em que o pagamento foi realizado.
     * @param moment O novo Instant do momento.
     */
    public void setMoment(Instant moment) {
       this.moment = moment;
    }

    /**
     * Retorna o pedido associado a este pagamento.
     * @return A entidade Order.
     */
    public Order getOrder() {
       return order;
    }

    /**
     * Define o pedido associado a este pagamento.
     * @param order A nova entidade Order.
     */
    public void setOrder(Order order) {
       this.order = order;
    }


    /**
     * Compara dois objetos Payment com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
       if (this == o) return true;
       if (!(o instanceof Payment)) return false;
       Payment payment = (Payment) o;
       return Objects.equals(getId(), payment.getId());
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
       return Objects.hash(getId());
    }
}