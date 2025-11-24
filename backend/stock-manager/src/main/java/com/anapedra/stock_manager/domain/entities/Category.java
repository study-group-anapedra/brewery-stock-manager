package com.anapedra.stock_manager.domain.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    
    // A descrição pode ser longa, use @Lob ou ajuste o length
    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Relationship: Many-to-Many with Beer (INVERSE SIDE) ---

    /**
     * O relacionamento Many-to-Many é mapeado de forma bidirecional.
     * Category é o lado INVERSO.
     * mappedBy="categories" aponta para o nome do campo 'categories' na classe Beer, 
     * que é o lado PROPRIETÁRIO e contém a anotação @JoinTable.
     */
   // @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    // Usar FetchType.LAZY é padrão e recomendado para coleções para evitar N+1 queries.
    //private Set<Beer> beers = new HashSet<>();

    // --- Constructors ---

    // 1. Construtor padrão obrigatório pelo JPA (public ou protected)
    public Category() {
        // Inicialização de 'beers' feita diretamente na declaração do campo acima
    }

    // 2. Construtor de conveniência
    public Category(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }

    // --- Getters and Setters ---
    
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

  //  public Set<Beer> getBeers() { return beers; }
    
    // Omitido setter para 'beers' para forçar o uso de métodos de auxílio (add/remove)
    
    
    // --- Utility Methods for Bidirectional Sync (Optional, but recommended) ---
   /*
    public void addBeer(Beer beer) {
        this.beers.add(beer);
        // Garante que o lado proprietário (Beer) também seja atualizado
        if (!beer.getCategories().contains(this)) {
            beer.getCategories().add(this);
        }
    }

    public void removeBeer(Beer beer) {
        this.beers.remove(beer);
        // Garante que o lado proprietário (Beer) também seja atualizado
        if (beer.getCategories().contains(this)) {
            beer.getCategories().remove(this);
        }
    }

    */

    // --- equals() and hashCode() ---

    // Essencial para entidades JPA: usar apenas o ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        // Cuidado: Evite acessar coleções lazy-loaded (beers.size()) fora de uma transação
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                //", totalBeers=" + beers.size() + // Acessado aqui para o exemplo, mas use com cautela
                '}';
    }
}