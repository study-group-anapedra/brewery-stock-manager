package com.anapedra.stock_manager.domain.pks;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Order;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderItemPK implements Serializable {
    private static final long serialVersionUID=1L;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "beer_id")
    private Beer beer;

    public OrderItemPK() {

    }

    public OrderItemPK(Order order, Beer beer) {
        this.order = order;
        this.beer = beer;
    }

    public OrderItemPK(Long orderId, Long beerId) {
        this.order = new Order();
        this.order.setId(orderId);
        this.beer = new Beer();
        this.beer.setId(beerId);
    }
    public OrderItemPK(Long beerId) {
        this.beer = new Beer();
        this.beer.setId(beerId);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemPK that = (OrderItemPK) o;
        return Objects.equals(order, that.order) && Objects.equals(beer, that.beer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, beer);
    }
}