package com.anapedra.stock_manager.domain.entities;

import com.anapedra.stock_manager.domain.enums.OrderStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa a entidade Pedido (Order).
 * Esta classe mapeia a tabela "tb_order" no banco de dados.
 *
 * <p>Contém informações sobre a transação, como momento da compra, status,
 * o cliente e a lista de itens comprados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único do pedido.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O momento exato (timestamp) em que o pedido foi feito.
     */
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant momentAt;

    /**
     * O cliente (User) que realizou o pedido. Relacionamento Many-to-One.
     */
    @ManyToOne
    @JoinColumn(name = "client_id") // CORRIGIDO: nome padrão para FK
    private User client;

    /**
     * O pagamento associado a este pedido. Relacionamento One-to-One.
     */
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    /**
     * O código inteiro que representa o status do pedido (mapeado para OrderStatus).
     */
    private Integer orderStatus;

    /**
     * O conjunto de itens (OrderItem) que compõem o pedido.
     */
    @OneToMany(mappedBy = "id.order", cascade = CascadeType.PERSIST)
    private Set<OrderItem> items = new HashSet<>();

    /**
     * Construtor padrão sem argumentos.
     */
    public Order() {
    }

    /**
     * Construtor para inicializar o pedido.
     *
     * @param momentAt O momento em que o pedido foi feito.
     * @param client O cliente que realizou o pedido.
     * @param orderStatus O status inicial do pedido.
     */
    public Order(Instant momentAt, User client, OrderStatus orderStatus) {
        this.momentAt = momentAt;
        this.client = client;
        setOrderStatus(orderStatus);
    }

    /**
     * Calcula a quantidade total de produtos (soma das quantidades de todos os OrderItems).
     * @return A soma das quantidades dos itens.
     */
    public int getQuantityProduct() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    /**
     * Calcula o valor total do pedido (soma dos subTotais de todos os OrderItems).
     * @return O valor total.
     */
    public double getTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
    }

    /**
     * Retorna o valor total a ser pago (atualmente, o mesmo que o total do pedido).
     * @return O valor total a pagar.
     */
    public double getTotalToPay() {
        return getTotal();
    }

    /**
     * Retorna o momento em que o pedido foi feito.
     * @return O Instant do momento.
     */
    public Instant getMomentAt() {
        return momentAt;
    }

    /**
     * Define o momento em que o pedido foi feito.
     * @param momentAt O novo Instant.
     */
    public void setMomentAt(Instant momentAt) {
        this.momentAt = momentAt;
    }

    /**
     * Retorna o ID do pedido.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID do pedido.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o cliente que realizou o pedido.
     * @return A entidade User do cliente.
     */
    public User getClient() {
        return client;
    }

    /**
     * Define o cliente que realizou o pedido.
     * @param client A nova entidade User do cliente.
     */
    public void setClient(User client) {
        this.client = client;
    }

    /**
     * Retorna o pagamento associado a este pedido.
     * @return A entidade Payment.
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Define o pagamento associado a este pedido.
     * @param payment A nova entidade Payment.
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * Retorna o status do pedido, mapeado do código inteiro para o Enum OrderStatus.
     * @return O OrderStatus. Retorna WAITING_PAYMENT se o status for nulo.
     */
    public OrderStatus getOrderStatus(){
        if (orderStatus != null){
            return OrderStatus.valueOf(orderStatus);
        }
        return OrderStatus.WAITING_PAYMENT;

    }

    /**
     * Define o status do pedido, mapeando o Enum OrderStatus para o código inteiro.
     * @param orderStatus O novo OrderStatus.
     */
    public void setOrderStatus(OrderStatus orderStatus){
        if (orderStatus != null){
            this.orderStatus = orderStatus.getCode();
        }
    }

    /**
     * Retorna o conjunto de itens do pedido.
     * @return O Set de OrderItem.
     */
    public Set<OrderItem> getItems() {
        return items;
    }

    /**
     * Define o conjunto de itens do pedido.
     * @param items O novo Set de OrderItem.
     */
    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }

    /**
     * Retorna a lista de cervejas (Beer) presentes neste pedido,
     * mapeando através dos itens do pedido.
     * @return A lista de Beer.
     */
    public List<Beer> getBeer() {
        return items.stream().map(OrderItem::getBeer).collect(Collectors.toList());
    }

    /**
     * Compara dois objetos Order com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}