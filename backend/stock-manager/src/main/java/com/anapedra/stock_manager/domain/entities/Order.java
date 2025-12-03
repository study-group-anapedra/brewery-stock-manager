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

@Entity
@Table(name = "tb_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant momentAt;

    @ManyToOne
    @JoinColumn(name = "client_id") // CORRIGIDO: nome padrão para FK
    private User client;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private Integer orderStatus;

    @OneToMany(mappedBy = "id.order", cascade = CascadeType.PERSIST)
    private Set<OrderItem> items = new HashSet<>();

    public Order() {
    }

    public Order(Instant momentAt, User client, OrderStatus orderStatus) {
        this.momentAt = momentAt;
        this.client = client;
        setOrderStatus(orderStatus);
    }

    public int getQuantityProduct() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
    }

    public double getTotalToPay() {
        return getTotal();
    }

    public Instant getMomentAt() {
        return momentAt;
    }

    public void setMomentAt(Instant momentAt) {
        this.momentAt = momentAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public OrderStatus getOrderStatus(){
        if (orderStatus != null){
            return OrderStatus.valueOf(orderStatus);
        }
        return OrderStatus.WAITING_PAYMENT;

    }

    public void setOrderStatus(OrderStatus orderStatus){
        if (orderStatus != null){
            this.orderStatus = orderStatus.getCode();
        }
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }

    public List<Beer> getBeer() {
        return items.stream().map(OrderItem::getBeer).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
