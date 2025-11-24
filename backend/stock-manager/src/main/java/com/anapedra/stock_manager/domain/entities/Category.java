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
    
    @Column(columnDefinition = "TEXT")
    private String description;


    /**
     * O relacionamento Many-to-Many é mapeado de forma bidirecional.
     * Category é o lado INVERSO.
     * mappedBy="categories" aponta para o nome do campo 'categories' na classe Beer, 
     * que é o lado PROPRIETÁRIO e contém a anotação @JoinTable.
     */
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Beer> beers = new HashSet<>();


    public Category() {
    }

    public Category(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

   public Set<Beer> getBeers() { return beers; }
    

    public void addBeer(Beer beer) {
        this.beers.add(beer);
        if (!beer.getCategories().contains(this)) {
            beer.getCategories().add(this);
        }
    }

    public void removeBeer(Beer beer) {
        this.beers.remove(beer);
        if (beer.getCategories().contains(this)) {
            beer.getCategories().remove(this);
        }
    }




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
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                //", totalBeers=" + beers.size() + // Acessado aqui para o exemplo, mas use com cautela
                '}';
    }
}