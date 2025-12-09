package com.anapedra.stock_manager.domain.entities;

import com.anapedra.stock_manager.domain.enums.StockStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa a entidade Estoque (Stock).
 * Esta classe mapeia a tabela "tb_stock" no banco de dados e gerencia a
 * quantidade disponível de uma cerveja específica.
 *
 * <p>O relacionamento é One-to-One e compartilha o ID com a entidade {@link Beer}
 * através da anotação {@code @MapsId}.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_stock")
public class Stock {

    /**
     * O identificador único do estoque, que é o mesmo ID da cerveja associada.
     */
    @Id
    @Column(name = "beer_id")
    private Long id;

    /**
     * A quantidade atual de cervejas em estoque.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * O momento da última atualização da quantidade em estoque.
     */
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    /**
     * O status atual do estoque (mapeado para o código do enum {@link StockStatus}).
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * O limite abaixo do qual o estoque é considerado baixo.
     */
    @Transient
    private static final int LOW_STOCK_LIMIT = 10;

    /**
     * A entidade Cerveja (Beer) associada a este estoque.
     * {@code @MapsId} garante que o ID do Stock seja o ID da Beer.
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Beer beer;


    /**
     * Construtor padrão. Inicializa a quantidade como 0, a data de atualização
     * como agora, e define o status.
     */
    public Stock() {
        if (this.quantity == null) {
             this.quantity = 0;
        }
        this.lastUpdate = LocalDateTime.now();
        updateStatus();
    }

    /**
     * Construtor para inicializar o objeto Stock.
     *
     * @param quantity A quantidade inicial.
     * @param beer A cerveja associada.
     */
    public Stock(Integer quantity, Beer beer) {
        this.quantity = (quantity != null) ? quantity : 0;
        this.lastUpdate = LocalDateTime.now();
        updateStatus();
        this.beer = beer;
    }


    /**
     * Zera o estoque se a cerveja associada estiver vencida (baseado no Beer.isExpired()).
     * @return A quantidade que foi removida (quantidade anterior), ou 0 se não estava vencida ou já estava zerada.
     */
public int checkAndClearIfExpired() {
    if (this.beer != null && this.beer.isExpired()) {
        int originalQuantity = this.quantity;
        if (originalQuantity > 0) {
            this.quantity = 0;
            this.lastUpdate = LocalDateTime.now();
            updateStatus();
            return originalQuantity;
        }
    }
    return 0;
}


/**
 * Executa antes da persistência ou atualização, garantindo que o timestamp
 * e o status sejam sempre atualizados.
 */
@PrePersist
@PreUpdate
protected void setTimestampsAndStatus() {
    if (this.lastUpdate == null) {
        this.lastUpdate = LocalDateTime.now();
    }
    if (this.quantity == null) {
        this.quantity = 0;
    }
    updateStatus();
}

/**
 * Método interno para recalcular e definir o status do estoque
 * com base na quantidade e no limite de estoque baixo (LOW_STOCK_LIMIT).
 */
private void updateStatus() {
    if (this.quantity <= 0) {
        this.status = StockStatus.OUT_OF_STOCK.getCode();
    } else if (this.quantity <= LOW_STOCK_LIMIT) {
        this.status = StockStatus.LOW.getCode();
    } else {
        this.status = StockStatus.AVAILABLE.getCode();
    }
}

/**
 * Retorna o ID do estoque (que é o mesmo ID da cerveja).
 * @return O ID.
 */
public Long getId() { return id; }

/**
 * Define o ID do estoque (Geralmente não é chamado diretamente, pois é mapeado via @MapsId).
 * @param id O novo ID.
 */
public void setId(Long id) { this.id = id; }

/**
 * Retorna a quantidade atual em estoque.
 * @return A quantidade.
 */
public Integer getQuantity() { return quantity; }

/**
 * Define a nova quantidade em estoque, atualizando o status e o timestamp.
 * @param quantity A nova quantidade.
 */
public void setQuantity(Integer quantity) {
    this.quantity = (quantity != null) ? quantity : 0;
    updateStatus();
    this.lastUpdate = LocalDateTime.now();
}

/**
 * Retorna o status do estoque como um Enum.
 * @return O {@link StockStatus}.
 */
public StockStatus getStatus() {
    return StockStatus.valueOf(this.status);
}

/**
 * Define o status do estoque usando um Enum.
 * @param status O novo {@link StockStatus}.
 */
public void setStatus(StockStatus status) {
    if (status != null) {
        this.status = status.getCode();
    }
}

/**
 * Retorna o momento da última atualização do estoque.
 * @return O {@link LocalDateTime} da última atualização.
 */
public LocalDateTime getLastUpdate() { return lastUpdate; }

/**
 * Define o momento da última atualização.
 * @param lastUpdate O novo {@link LocalDateTime}.
 */
public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

/**
 * Retorna a cerveja associada a este estoque.
 * @return A entidade {@link Beer}.
 */
public Beer getBeer() { return beer; }

/**
 * Define a cerveja associada a este estoque, mantendo a referência bidirecional
 * e copiando o ID da cerveja para o ID do estoque.
 * @param beer A nova entidade {@link Beer} associada.
 */
public void setBeer(Beer beer) {
    this.beer = beer;
    if (beer != null && !this.equals(beer.getStock())) {
        beer.setStock(this);
    }
    if (beer != null) {
        this.id = beer.getId();
    }
}



/**
 * Diminui a quantidade em estoque após uma venda.
 * @param amount Quantidade vendida (deve ser > 0).
 * @throws IllegalArgumentException Se a quantidade a ser diminuída não for positiva.
 * @throws IllegalStateException Se a quantidade em estoque for insuficiente.
 */
public void decreaseQuantity(int amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("A quantidade a ser diminuída deve ser positiva.");
    }
    if (this.quantity < amount) {
        // Lança uma exceção customizada de negócio (preferível) ou IllegalStateException
        throw new IllegalStateException("Estoque insuficiente para a cerveja: " + beer.getName() +
                ". Disponível: " + this.quantity + ", Pedido: " + amount);
    }
    this.quantity -= amount;
    this.lastUpdate = LocalDateTime.now();
    updateStatus(); // Recalcula o status
}

/**
 * Aumenta a quantidade em estoque (reposição/entrada).
 * @param amount Quantidade reposta (deve ser > 0).
 * @throws IllegalArgumentException Se a quantidade a ser aumentada não for positiva.
 */
public void increaseQuantity(int amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("A quantidade a ser aumentada deve ser positiva.");
    }
    this.quantity += amount;
    this.lastUpdate = LocalDateTime.now();
    updateStatus(); // Recalcula o status
}


/**
 * Compara dois objetos Stock com base no ID.
 * @param o O objeto a ser comparado.
 * @return true se os IDs forem iguais, false caso contrário.
 */
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Stock stock = (Stock) o;
    return id != null && Objects.equals(id, stock.id);
}

/**
 * Calcula o hash code com base no ID.
 * @return O hash code do ID.
 */
@Override
public int hashCode() {
    return id != null ? Objects.hash(id) : 31;
}

/**
 * Retorna uma representação em String deste estoque.
 * @return A String contendo o ID, quantidade, status e última atualização.
 */
@Override
public String toString() {
    return "Stock{" +
            "id=" + id +
            ", quantity=" + quantity +
            ", status=" + status +
            ", lastUpdate=" + lastUpdate +
            '}';
}
}