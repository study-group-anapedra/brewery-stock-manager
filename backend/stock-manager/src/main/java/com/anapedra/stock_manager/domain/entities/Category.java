package com.anapedra.stock_manager.domain.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa a entidade Categoria (Category).
 * Esta classe mapeia a tabela "tb_category" no banco de dados.
 *
 * <p>Define as categorias de cervejas, como Lager, IPA, etc.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_category")
public class Category {

    /**
     * O identificador único da categoria.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome da categoria (ex: "Lager").
     */
    @Column(name = "name")
    private String name;

    /**
     * A descrição detalhada da categoria.
     */
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


    /**
     * Construtor padrão sem argumentos.
     */
    public Category() {
    }

    /**
     * Construtor para inicializar todos os campos.
     *
     * @param id O identificador da categoria.
     * @param name O nome da categoria.
     * @param description A descrição da categoria.
     */
    public Category(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }


    /**
     * Retorna o ID da categoria.
     * @return O ID.
     */
    public Long getId() { return id; }

    /**
     * Define o ID da categoria.
     * @param id O novo ID.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna o nome da categoria.
     * @return O nome.
     */
    public String getName() { return name; }

    /**
     * Define o nome da categoria.
     * @param name O novo nome.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Retorna a descrição da categoria.
     * @return A descrição.
     */
    public String getDescription() { return description; }

    /**
     * Define a descrição da categoria.
     * @param description A nova descrição.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Retorna o conjunto de cervejas associadas a esta categoria.
     * @return O Set de Beer.
     */
   public Set<Beer> getBeers() { return beers; }


    /**
     * Adiciona uma cerveja ao conjunto de cervejas desta categoria,
     * mantendo a relação bidirecional.
     * @param beer A cerveja a ser adicionada.
     */
    public void addBeer(Beer beer) {
        this.beers.add(beer);
        if (!beer.getCategories().contains(this)) {
            beer.getCategories().add(this);
        }
    }

    /**
     * Remove uma cerveja do conjunto de cervejas desta categoria,
     * mantendo a relação bidirecional.
     * @param beer A cerveja a ser removida.
     */
    public void removeBeer(Beer beer) {
        this.beers.remove(beer);
        if (beer.getCategories().contains(this)) {
            beer.getCategories().remove(this);
        }
    }


    /**
     * Compara dois objetos Category com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && Objects.equals(id, category.id);
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retorna uma representação em String desta categoria.
     * @return A String contendo o ID, nome e descrição.
     */
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