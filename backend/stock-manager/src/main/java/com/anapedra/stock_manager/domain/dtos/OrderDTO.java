package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * DTO (Data Transfer Object) para a entidade Pedido (Order).
 *
 * <p>Esta classe transfere a visão completa de um pedido, incluindo detalhes
 * do cliente, o momento da criação, totais de cálculo, status, pagamento
 * e uma lista dos itens do pedido ({@link OrderItemDTO}).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see OrderItemDTO
 * @see PaymentDTO
 * @since 0.0.1-SNAPSHOT
 */
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único do pedido.
     */
    private Long id;

    /**
     * O ID do cliente que fez o pedido.
     */
    private Long clientId;

    /**
     * O nome do cliente.
     */
    private String clientName;

    /**
     * O CPF do cliente.
     */
    private String clientCpf;

    /**
     * O momento em que o pedido foi criado/registrado.
     */
    private Instant momentAt;

    /**
     * O valor total dos itens (sem descontos/acréscimos, se houver).
     */
    private Double total;

    /**
     * A quantidade total de produtos no pedido.
     */
    private Integer totalQuantity;

    /**
     * O valor final a ser pago pelo cliente (após cálculos).
     */
    private Double totalToPay;

    /**
     * O status atual do pedido (ex: WAITING_PAYMENT, PAID, etc.).
     */
    private OrderStatus orderStatus;

    /**
     * O DTO do pagamento associado a este pedido.
     */
    private PaymentDTO payment;


    /**
     * A lista de itens que compõem o pedido.
     * Deve conter ao menos um item.
     */
    @NotEmpty(message = "O pedido deve conter ao menos um item")
    private List<OrderItemDTO> items = new ArrayList<>();

    /**
     * Construtor padrão sem argumentos.
     */
    public OrderDTO() {}

    /**
     * Construtor para criação de um novo pedido.
     */
    public OrderDTO(Long clientId ,Instant momentAt,OrderStatus orderStatus,PaymentDTO payment) {
        this.clientId = clientId;
        this.momentAt = momentAt;
        this.orderStatus = orderStatus;
        this.payment = payment;
    }

    /**
     * Construtor que inicializa o DTO a partir da entidade {@link Order}.
     *
     * @param entity A entidade Order de origem.
     */
    public OrderDTO(Order entity) {
        id = entity.getId();
        clientId = entity.getClient().getId();
        clientName = entity.getClient().getName();
        clientCpf = entity.getClient().getCpf();
        momentAt = entity.getMomentAt();
        total = entity.getTotal();
        totalQuantity = entity.getQuantityProduct();
        totalToPay = entity.getTotalToPay();

        // Lógica para garantir que o status não seja nulo (default para WAITING_PAYMENT)
        orderStatus = (entity.getPayment() == null || entity.getOrderStatus() == null)
                ? OrderStatus.WAITING_PAYMENT
                : entity.getOrderStatus() ;

        // Converte a entidade Payment para PaymentDTO, se existir
        payment = (entity.getPayment() != null) ? new PaymentDTO(entity.getPayment()) : null;

        // Converte as entidades OrderItem para OrderItemDTO
        entity.getItems().forEach(item ->
                this.items.add(new OrderItemDTO(item))
        );
    }

    /**
     * Construtor que inicializa o DTO a partir da entidade {@link Order} e um Set de {@link OrderItem}.
     * Usado para garantir que os itens sejam carregados (útil se o Set for carregado lazy).
     *
     * @param entity A entidade Order de origem.
     * @param orderItems O Set de OrderItem a ser incluído.
     */
    public OrderDTO(Order entity, Set<OrderItem> orderItems) {
        this(entity);
        orderItems.forEach(item -> this.items.add(new OrderItemDTO(item)));
    }

    // --- Getters e Setters ---

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
     * Retorna o ID do cliente.
     * @return O ID do cliente.
     */
    public Long getClientId() {
        return clientId;
    }

    /**
     * Define o ID do cliente.
     * @param clientId O novo ID do cliente.
     */
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    /**
     * Retorna o nome do cliente.
     * @return O nome do cliente.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Define o nome do cliente.
     * @param clientName O novo nome do cliente.
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Retorna o CPF do cliente.
     * @return O CPF do cliente.
     */
    public String getClientCpf() {
        return clientCpf;
    }

    /**
     * Define o CPF do cliente.
     * @param clientCpf O novo CPF do cliente.
     */
    public void setClientCpf(String clientCpf) {
        this.clientCpf = clientCpf;
    }

    /**
     * Retorna o momento em que o pedido foi criado.
     * @return O Instant da criação.
     */
    public Instant getMomentAt() {
        return momentAt;
    }

    /**
     * Define o momento em que o pedido foi criado.
     * @param momentAt O novo Instant.
     */
    public void setMomentAt(Instant momentAt) {
        this.momentAt = momentAt;
    }

    /**
     * Retorna o valor total dos itens.
     * @return O total.
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Define o valor total dos itens.
     * @param total O novo total.
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * Retorna a quantidade total de produtos.
     * @return A quantidade total.
     */
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * Define a quantidade total de produtos.
     * @param totalQuantity A nova quantidade total.
     */
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    /**
     * Retorna o valor final a ser pago.
     * @return O total a pagar.
     */
    public Double getTotalToPay() {
        return totalToPay;
    }

    /**
     * Define o valor final a ser pago.
     * @param totalToPay O novo total a pagar.
     */
    public void setTotalToPay(Double totalToPay) {
        this.totalToPay = totalToPay;
    }

    /**
     * Retorna o status do pedido.
     * @return O {@link OrderStatus}.
     */
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    /**
     * Define o status do pedido.
     * @param orderStatus O novo {@link OrderStatus}.
     */
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * Retorna o DTO do pagamento.
     * @return O {@link PaymentDTO}.
     */
    public PaymentDTO getPayment() {
        return payment;
    }

    /**
     * Define o DTO do pagamento.
     * @param payment O novo {@link PaymentDTO}.
     */
    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }

    /**
     * Retorna a lista de itens do pedido.
     * @return A lista de {@link OrderItemDTO}.
     */
    public List<OrderItemDTO> getItems() {
        return items;
    }

    /**
     * Define a lista de itens do pedido.
     * @param items A nova lista de {@link OrderItemDTO}.
     */
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    /**
     * Compara dois objetos OrderDTO com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDTO other)) return false;
        return Objects.equals(id, other.id);
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