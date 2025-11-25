package com.anapedra.stock_manager.domain.entities;

import com.anapedra.stock_manager.domain.pks.OrderItemPK;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
	private static final long serialVersionUID=1L;


	@EmbeddedId
	private OrderItemPK id=new OrderItemPK();

	private Integer quantity;
    private Double price;

	public OrderItem() {

	}

	public OrderItem(Order order,Beer beer,Integer quantity) {
		id.setOrder(order);
		id.setBeer(beer);
		this.quantity = quantity;

	}


	public OrderItem(Order order,Beer beer,Integer quantity,Double price) {
		id.setOrder(order);
		id.setBeer(beer);
		this.quantity = quantity;
		this.price = price;

	}

    public double getSubTotal(){
		return quantity * getBeer().getPrice();
	}


/*

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw  new IllegalArgumentException("Quantity must be greater than zero");
        }
        Stock stock = getBeer().getStock();

        Integer newQuantity = stock.getQuantity() - quantity;

        if (newQuantity < 0) {
            throw new IllegalStateException("Insufficient stock for Beer: " + getBeer().getName());
        }
        stock.setQuantity(newQuantity);

    }

 */

    public OrderItemPK getId() {
        return id;
    }

    public Order getOrder(){
		return id.getOrder();
	}
	public void setOrder(Order order){
		id.setOrder(order);
	}
	public Beer getBeer(){
		return id.getBeer();
	}
	public void setBeer(Beer beer){
		id.setBeer(beer);
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderItem)) return false;
		OrderItem orderItem = (OrderItem) o;
		return Objects.equals(id, orderItem.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}

