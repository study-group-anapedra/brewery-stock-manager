package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.OrderItem;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID=1L;

    private Long beerId;
    private String title;
    private Integer quantity;
    private Double beerPrice;
    private Double subTotal;
    private String imgUrl;

    public OrderItemDTO() {

    }

    public OrderItemDTO(Long beerId,Integer quantity) {
        this.beerId = beerId;
        this.quantity = quantity;


    }

    public OrderItemDTO(OrderItem entity) {
       beerId = entity.getBeer().getId();
       title = entity.getBeer().getName();
       quantity = entity.getQuantity();
       subTotal=entity.getSubTotal();
       beerPrice = entity.getBeer().getPrice();
       imgUrl=entity.getBeer().getUrlImg();

    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public Double getBeerPrice() {
        return beerPrice;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDTO that = (OrderItemDTO) o;
        return Objects.equals(beerId, that.beerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(beerId);
    }
}


