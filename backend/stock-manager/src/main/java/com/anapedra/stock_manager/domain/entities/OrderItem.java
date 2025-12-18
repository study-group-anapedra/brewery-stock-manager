package com.anapedra.stock_manager.domain.entities;

import com.anapedra.stock_manager.domain.enums.StockStatus;
import com.anapedra.stock_manager.domain.pks.OrderItemPK;
import com.anapedra.stock_manager.services.exceptions.InsufficientStockException;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um Item de Pedido (OrderItem).
 * Esta é uma entidade de relacionamento Many-to-Many com atributos extras,
 * usando uma chave primária composta (`OrderItemPK`).
 *
 * <p>Mapeia a tabela "tb_order_item" e armazena a quantidade e o preço
 * de uma `Beer` específica em um `Order` específico.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
    private static final long serialVersionUID=1L;


    /**
     * Chave primária composta (EmbeddedId) que contém referências ao Order e à Beer.
     */
    @EmbeddedId
    private OrderItemPK id=new OrderItemPK();

    /**
     * A quantidade da cerveja neste item do pedido.
     */
    private Integer quantity;

    /**
     * O preço unitário da cerveja no momento do pedido.
     */
    private Double price;

    /**
     * Construtor padrão sem argumentos.
     */
    public OrderItem() {

    }

    /**
     * Construtor para inicializar o OrderItem (versão simplificada).
     * O preço é normalmente obtido do produto no momento da criação.
     *
     * @param order O pedido ao qual o item pertence.
     * @param beer A cerveja que é o item.
     * @param quantity A quantidade do item.
     */
    public OrderItem(Order order,Beer beer,Integer quantity) {
       id.setOrder(order);
       id.setBeer(beer);
       this.quantity = quantity;

    }


    /**
     * Construtor para inicializar o OrderItem com todos os atributos.
     *
     * @param order O pedido ao qual o item pertence.
     * @param product A cerveja (produto) que é o item.
     * @param quantity A quantidade do item.
     * @param price O preço unitário da cerveja no momento da criação do item.
     */
    public OrderItem(Order order,Beer product,Integer quantity, Double price) {
        id.setOrder(order);
        id.setBeer(product);
        this.quantity = quantity;
        this.price = price;
    }


    /**
     * Calcula o subtotal para este item do pedido (quantidade * preço unitário da Beer).
     * @return O subtotal.
     */
    public double getSubTotal(){
       return quantity * getBeer().getPrice();
    }

    public void setAtualStock() {
        getBeer().getStock().subtractQuantity(this.quantity);
    }





    /**
     * Retorna a chave primária composta (OrderItemPK).
     * @return A chave composta.
     */
    public OrderItemPK getId() {
        return id;
    }

    /**
     * Retorna o Pedido (Order) associado a este item.
     * @return A entidade Order.
     */
    public Order getOrder(){
       return id.getOrder();
    }

    /**
     * Define o Pedido (Order) associado a este item.
     * @param order A entidade Order.
     */
    public void setOrder(Order order){
       id.setOrder(order);
    }

    /**
     * Retorna a Cerveja (Beer) associada a este item.
     * @return A entidade Beer.
     */
    public Beer getBeer(){
       return id.getBeer();
    }

    /**
     * Define a Cerveja (Beer) associada a este item.
     * @param beer A entidade Beer.
     */
    public void setBeer(Beer beer){
       id.setBeer(beer);
    }

    /**
     * Retorna a quantidade deste item.
     * @return A quantidade.
     */
    public Integer getQuantity() {
       return quantity;
    }

    /**
     * Define a quantidade deste item.
     * @param quantity A nova quantidade.
     */
    public void setQuantity(Integer quantity) {
       this.quantity = quantity;
    }

    /**
     * Retorna o preço unitário da cerveja no momento do pedido.
     * @return O preço.
     */
    public Double getPrice() {
       return price;
    }

    /**
     * Define o preço unitário da cerveja no momento do pedido.
     * @param price O novo preço.
     */
    public void setPrice(Double price) {
       this.price = price;
    }

    /**
     * Compara dois objetos OrderItem com base na chave primária composta (id).
     * @param o O objeto a ser comparado.
     * @return true se as chaves forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
       if (this == o) return true;
       if (!(o instanceof OrderItem)) return false;
       OrderItem orderItem = (OrderItem) o;
       return Objects.equals(id, orderItem.id);
    }

    /**
     * Calcula o hash code com base na chave primária composta (id).
     * @return O hash code.
     */
    @Override
    public int hashCode() {
       return Objects.hash(id);
    }








}