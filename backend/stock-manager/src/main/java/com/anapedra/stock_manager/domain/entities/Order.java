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
    private static final long serialVersionUID=1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    //@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "GMT")
    private Instant momentAt;
    private Integer orderStatus;

    @ManyToOne
    @JoinColumn(name = "clientId")
    private User client;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "id.order", cascade = CascadeType.PERSIST)
    private Set<OrderItem> items = new HashSet<>();

    public Order(Long id,Instant momentAt, User client) {
        this.id = id;
        this.momentAt = momentAt;
        this.client = client;
        setOrderStatus(OrderStatus.WAITING_PAYMENT);

    }

    public Order() {

    }

    public int getQuantityProduct(){
        int soma = 0;
        for (OrderItem orderItem : items) {
            soma += orderItem.getQuantity();
        }
        return soma;
    }

    public double getTotal(){
        double soma = 0.0;
        for (OrderItem orderItem : items) {
            soma += orderItem.getSubTotal();
        }
        return soma;

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

    public OrderStatus getOrderStatus() {
        return OrderStatus.valueOf(orderStatus);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        if (orderStatus != null){
            this.orderStatus = orderStatus.getCode();
        }
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

    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }

    public Set<OrderItem> getItems() {
        return items;

    }





    public List<Beer> getBeer(){
        return items.stream().map(x->x.getBeer()).collect(Collectors.toList());
    }




    public double getTotalToPay() {
        double total = getTotal();

       return total;
    }






@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order)) return false;
    Order order = (Order) o;
    return Objects.equals(getId(), order.getId());
}

@Override
public int hashCode() {
    return Objects.hash(getId());
}
}