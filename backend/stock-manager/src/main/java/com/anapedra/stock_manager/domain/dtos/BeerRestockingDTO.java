package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.BeerRestocking;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class BeerRestockingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long beerId;
    private Integer quantity;

    

    public BeerRestockingDTO() {
    }

    public BeerRestockingDTO(Long beerId, Integer quantity){
        this.beerId = beerId;
        this.quantity = quantity;

    }

    public BeerRestockingDTO(BeerRestocking entity) {
        beerId = entity.getBeer().getId();
        quantity = entity.getQuantity();




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
}