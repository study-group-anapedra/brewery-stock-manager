package com.anapedra.stock_manager.domain.pks;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Order;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa a Chave Primária Composta (Primary Key) da entidade {@link com.anapedra.stock_manager.domain.entities.OrderItem}.
 *
 * <p>Esta classe é usada em conjunto com {@code @EmbeddedId} e contém as referências
 * às entidades {@link Order} e {@link Beer} que formam a chave única.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Embeddable
public class OrderItemPK implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * A referência ao Pedido (Order) que faz parte da chave.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * A referência à Cerveja (Beer) que faz parte da chave.
     */
    @ManyToOne
    @JoinColumn(name = "beer_id")
    private Beer beer;

    /**
     * Construtor padrão sem argumentos.
     */
    public OrderItemPK() {

    }

    /**
     * Construtor para inicializar a chave com as entidades completas.
     *
     * @param order A entidade Order.
     * @param beer A entidade Beer.
     */
    public OrderItemPK(Order order, Beer beer) {
        this.order = order;
        this.beer = beer;
    }

    /**
     * Construtor para inicializar a chave apenas com os IDs das entidades,
     * criando stubs de Order e Beer.
     *
     * @param orderId O ID do Order.
     * @param beerId O ID da Beer.
     */
    public OrderItemPK(Long orderId, Long beerId) {
        this.order = new Order();
        this.order.setId(orderId);
        this.beer = new Beer();
        this.beer.setId(beerId);
    }

    /**
     * Construtor para inicializar a chave apenas com o ID da cerveja,
     * criando um stub de Beer. Útil em contextos específicos de busca.
     *
     * @param beerId O ID da Beer.
     */
    public OrderItemPK(Long beerId) {
        this.beer = new Beer();
        this.beer.setId(beerId);
    }

    /**
     * Retorna o Pedido (Order) desta chave.
     * @return A entidade Order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Define o Pedido (Order) desta chave.
     * @param order A nova entidade Order.
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Retorna a Cerveja (Beer) desta chave.
     * @return A entidade Beer.
     */
    public Beer getBeer() {
        return beer;
    }

    /**
     * Define a Cerveja (Beer) desta chave.
     * @param beer A nova entidade Beer.
     */
    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    /**
     * Compara dois objetos OrderItemPK. A igualdade depende da igualdade
     * das entidades Order e Beer que compõem a chave.
     *
     * @param o O objeto a ser comparado.
     * @return true se as chaves compostas forem iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemPK that = (OrderItemPK) o;
        return Objects.equals(order, that.order) && Objects.equals(beer, that.beer);
    }

    /**
     * Calcula o hash code com base nas entidades Order e Beer.
     *
     * @return O hash code combinado.
     */
    @Override
    public int hashCode() {
        return Objects.hash(order, beer);
    }
}