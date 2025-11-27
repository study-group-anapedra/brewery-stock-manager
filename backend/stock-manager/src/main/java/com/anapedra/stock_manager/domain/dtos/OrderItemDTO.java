package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.OrderItem;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID=1L;

    private Long beerId;
    private String title;
    private Integer quantity;
    private Double bookPrice;
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
       bookPrice = entity.getBeer().getPrice();
       imgUrl=entity.getBeer().getUrlImg();

    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long livroId) {
        this.beerId = beerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Double getBookPrice() {
        return bookPrice;
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


