package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Order;
import com.anapedra.stock_manager.domain.entities.OrderItem;
import com.anapedra.stock_manager.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String clientName;
    private String clientCpf;
    private Long clientId;

    private Instant momentAt;

    private Double total;
    private Integer totalQuantity;
    private Double totalToPay;
    @NotNull(message = "Status do pedido é obrigatório")
    private OrderStatus orderStatus;
    
    private PaymentDTO payment;

    @NotEmpty(message = "O pedido deve conter ao menos um item")
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO() {
    }

    public OrderDTO(Long id,Instant momentAt, Long clientId,
                    OrderStatus orderStatus, PaymentDTO payment) {
        this.id = id;
        this.momentAt = momentAt;
        this.clientId = clientId;
        this.orderStatus = orderStatus;
        this.payment = payment;
    }

    public OrderDTO(Order entity) {
        this.id = entity.getId();
        this.clientName = entity.getClient().getName();
        this.clientCpf = entity.getClient().getCpf();
        this.clientId = entity.getClient().getId();
        this.momentAt = entity.getMomentAt();
        this.momentAt = entity.getMomentAt();
        this.total = entity.getTotal();
        this.totalQuantity = entity.getQuantityProduct();
        this.totalToPay = entity.getTotalToPay();
        this.orderStatus = entity.getOrderStatus() != null ? entity.getOrderStatus() : OrderStatus.WAITING_PAYMENT;
        
        if (entity.getPayment() != null) {
            this.payment = new PaymentDTO(entity.getPayment());
        }
    }

    public OrderDTO(Order entity, Set<OrderItem> orderItems) {
        this(entity);
        orderItems.forEach(item -> this.items.add(new OrderItemDTO(item)));
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
        if (!(o instanceof OrderDTO orderDTO)) return false;
        return Objects.equals(id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}