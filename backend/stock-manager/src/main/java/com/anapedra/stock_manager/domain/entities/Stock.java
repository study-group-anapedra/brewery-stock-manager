package com.anapedra.stock_manager.domain.entities;

import com.anapedra.stock_manager.domain.enums.StockStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @Column(name = "beer_id")
    private Long id;

    @Column(nullable = false)
    private Integer quantity; 

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    
    @Column(nullable = false)
    private Integer status;

    @Transient
    private static final int LOW_STOCK_LIMIT = 10;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Beer beer;



    public Stock() {
        if (this.quantity == null) {
             this.quantity = 0;
        }
        this.lastUpdate = LocalDateTime.now();
        updateStatus(); 
    }

    public Stock(Integer quantity, Beer beer) {
        this.quantity = (quantity != null) ? quantity : 0; 
        this.lastUpdate = LocalDateTime.now(); 
        updateStatus(); 
        this.beer = beer;
    }




    /**
     * Zera o estoque se a cerveja associada estiver vencida.
     * @return A quantidade que foi removida (quantidade anterior), ou 0 se não estava vencida.
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

private void updateStatus() {
    if (this.quantity <= 0) {
        this.status = StockStatus.OUT_OF_STOCK.getCode();
    } else if (this.quantity <= LOW_STOCK_LIMIT) {
        this.status = StockStatus.LOW.getCode();
    } else {
        this.status = StockStatus.AVAILABLE.getCode();
    }
}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public Integer getQuantity() { return quantity; }

public void setQuantity(Integer quantity) {
    this.quantity = (quantity != null) ? quantity : 0;
    updateStatus();
    this.lastUpdate = LocalDateTime.now();
}

public StockStatus getStatus() {
    return StockStatus.valueOf(this.status);
}

public void setStatus(StockStatus status) {
    if (status != null) {
        this.status = status.getCode();
    }
}

public LocalDateTime getLastUpdate() { return lastUpdate; }
public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

public Beer getBeer() { return beer; }

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
 * @throws IllegalStateException se a quantidade em estoque for insuficiente.
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
 */
public void increaseQuantity(int amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("A quantidade a ser aumentada deve ser positiva.");
    }
    this.quantity += amount;
    this.lastUpdate = LocalDateTime.now();
    updateStatus(); // Recalcula o status
}



@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Stock stock = (Stock) o;
    return id != null && Objects.equals(id, stock.id);
}

@Override
public int hashCode() {
    return id != null ? Objects.hash(id) : 31;
}

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