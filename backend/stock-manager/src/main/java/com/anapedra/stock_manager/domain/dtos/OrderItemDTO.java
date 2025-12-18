package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.OrderItem;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) para a entidade Item de Pedido (OrderItem).
 *
 * <p>Usado para transferir os detalhes de um produto dentro de um pedido,
 * incluindo informações essenciais da cerveja (ID, nome, preço), quantidade,
 * e o subtotal calculado.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * O ID da cerveja (Beer) associada a este item.
     */
    private Long beerId;

    /**
     * O nome da cerveja (para exibição).
     */
    private String title;

    /**
     * A quantidade comprada deste item.
     */
    private Integer quantity;

    /**
     * O preço unitário da cerveja no momento da compra.
     */
    private Double beerPrice;

    /**
     * O subtotal deste item (quantidade * preço unitário).
     */
    private Double subTotal;

    /**
     * A URL da imagem da cerveja.
     */
    private String imgUrl;

    /**
     * Construtor padrão sem argumentos.
     */
    public OrderItemDTO() {

    }

    /**
     * Construtor para inicializar DTOs de entrada (Input DTOs),
     * onde apenas o ID do produto e a quantidade são conhecidos.
     *
     * @param beerId O ID da cerveja.
     * @param quantity A quantidade do item.
     */
    public OrderItemDTO(Long beerId,Integer quantity) {
        this.beerId = beerId;
        this.quantity = quantity;

    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link OrderItem}.
     *
     * @param entity A entidade OrderItem de origem.
     */
    public OrderItemDTO(OrderItem entity) {
       // Assume que a referência Beer está carregada
       beerId = entity.getBeer().getId();
       title = entity.getBeer().getName();
       quantity = entity.getQuantity();
       subTotal=entity.getSubTotal();
       beerPrice = entity.getBeer().getPrice();
       imgUrl=entity.getBeer().getUrlImg();

    }

    /**
     * Retorna o ID da cerveja.
     * @return O ID da cerveja.
     */
    public Long getBeerId() {
        return beerId;
    }

    /**
     * Define o ID da cerveja.
     * @param beerId O novo ID da cerveja.
     */
    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    /**
     * Retorna a quantidade comprada.
     * @return A quantidade.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Define a quantidade comprada.
     * @param quantity A nova quantidade.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Retorna o nome/título da cerveja.
     * @return O nome.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retorna o preço unitário da cerveja.
     * @return O preço unitário.
     */
    public Double getBeerPrice() {
        return beerPrice;
    }

    /**
     * Retorna o subtotal do item (preço total para a quantidade comprada).
     * @return O subtotal.
     */
    public Double getSubTotal() {
        return subTotal;
    }

    /**
     * Retorna a URL da imagem da cerveja.
     * @return A URL da imagem.
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * Compara dois objetos OrderItemDTO com base no ID da cerveja.
     * @param o O objeto a ser comparado.
     * @return true se os IDs das cervejas forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDTO that = (OrderItemDTO) o;
        return Objects.equals(beerId, that.beerId);
    }

    /**
     * Calcula o hash code com base no ID da cerveja.
     * @return O hash code do ID da cerveja.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(beerId);
    }
}