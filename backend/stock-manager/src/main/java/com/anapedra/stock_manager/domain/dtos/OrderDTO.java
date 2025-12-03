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

public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long clientId;
    private String clientName;
    private String clientCpf;
    private Instant momentAt;
    private Double total;
    private Integer totalQuantity;
    private Double totalToPay;
    private OrderStatus orderStatus;
    private PaymentDTO payment;



    @NotEmpty(message = "O pedido deve conter ao menos um item")
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO() {}

    public OrderDTO(Long clientId ,Instant momentAt,OrderStatus orderStatus,PaymentDTO payment) {
        this.clientId = clientId;
        this.momentAt = momentAt;
        this.orderStatus = orderStatus;
        this.payment = payment;

    }

    public OrderDTO(Order entity) {
        id = entity.getId();
        clientId = entity.getClient().getId();
        clientName = entity.getClient().getName();
        clientCpf = entity.getClient().getCpf();
        momentAt = entity.getMomentAt();
        total = entity.getTotal();
        totalQuantity = entity.getQuantityProduct();
        totalToPay = entity.getTotalToPay();
        orderStatus = (entity.getPayment() == null || entity.getOrderStatus() == null) ? OrderStatus.WAITING_PAYMENT : entity.getOrderStatus() ;
        payment = (entity.getPayment() != null) ? new PaymentDTO(entity.getPayment()) : null;
        entity.getItems().forEach(item ->
                this.items.add(new OrderItemDTO(item))
        );




    }

    public OrderDTO(Order entity, Set<OrderItem> orderItems) {
        this(entity);
        orderItems.forEach(item -> this.items.add(new OrderItemDTO(item)));
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCpf() {
        return clientCpf;
    }

    public void setClientCpf(String clientCpf) {
        this.clientCpf = clientCpf;
    }

    public Instant getMomentAt() {
        return momentAt;
    }

    public void setMomentAt(Instant momentAt) {
        this.momentAt = momentAt;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getTotalToPay() {
        return totalToPay;
    }

    public void setTotalToPay(Double totalToPay) {
        this.totalToPay = totalToPay;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDTO other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}



