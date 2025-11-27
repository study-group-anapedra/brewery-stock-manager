package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Payment;

import java.io.Serializable;
import java.time.Instant;

public class PaymentDTO implements Serializable {
    private static final long serialVersionUID=1L;
    public Long id;
    private Instant moment;
    private Double totalPayment;
    private OrderDTO order;

    public PaymentDTO() {

    }

    public PaymentDTO(Long id, Instant moment,OrderDTO order) {
        this.id = id;
        this.moment = moment;
        this.order = order;
        this.totalPayment = order.getTotal();

    }

    public PaymentDTO(Payment entity) {
        this.id = entity.getId();
        moment = entity.getMoment();
        totalPayment=  entity.getOrder().getTotal();

    }


    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }


}



