package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Beer;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * DTO (Data Transfer Object) usado para filtrar, listar ou exibir informações
 * essenciais de uma {@link Beer}, incluindo a quantidade em estoque.
 *
 * <p>Ideal para ser usado em endpoints de busca ou listagem de produtos.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
public class BeerFilterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador único da cerveja.
     */
    private Long id;

    /**
     * O nome da cerveja.
     */
    private String name;

    /**
     * A URL da imagem da cerveja.
     */
    private String urlImg;

    /**
     * O teor alcoólico da cerveja.
     */
    private Double alcoholContent;

    /**
     * O preço unitário da cerveja.
     */
    private Double price;

    /**
     * A data de fabricação da cerveja.
     */
    private LocalDate manufactureDate;

    /**
     * A data de validade da cerveja.
     */
    private LocalDate expirationDate;

    /**
     * O conjunto de categorias, usando o DTO simplificado {@link MinCategoryDTO}.
     */
    private Set<MinCategoryDTO> categories = new HashSet<>();

    /**
     * A quantidade atual em estoque.
     */
    private Integer stock;

    /**
     * Construtor padrão sem argumentos.
     */
    public BeerFilterDTO() {
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Beer}.
     *
     * @param entity A entidade Beer de origem.
     */
    public BeerFilterDTO(Beer entity) {
        id = entity.getId();
        name = entity.getName();
        urlImg = entity.getUrlImg();
        alcoholContent = entity.getAlcoholContent();
        price = entity.getPrice();
        manufactureDate = entity.getManufactureDate();
        expirationDate = entity.getExpirationDate();
        stock = entity.returnQuantityStock();
        // Converte as categorias para o formato simplificado MinCategoryDTO
        entity.getCategories().forEach(category -> this.categories.add(new MinCategoryDTO(category)));
    }

    /**
     * Retorna o ID da cerveja.
     * @return O ID.
     */
    public Long getId() { return id; }

    /**
     * Retorna o nome da cerveja.
     * @return O nome.
     */
    public String getName() { return name; }

    /**
     * Retorna a URL da imagem.
     * @return A URL da imagem.
     */
    public String getUrlImg() { return urlImg; }

    /**
     * Retorna o teor alcoólico.
     * @return O teor alcoólico.
     */
    public Double getAlcoholContent() { return alcoholContent; }

    /**
     * Retorna o preço.
     * @return O preço.
     */
    public Double getPrice() { return price; }

    /**
     * Retorna a data de fabricação.
     * @return A data de fabricação.
     */
    public LocalDate getManufactureDate() { return manufactureDate; }

    /**
     * Retorna a data de validade.
     * @return A data de validade.
     */
    public LocalDate getExpirationDate() { return expirationDate; }

    /**
     * Retorna as categorias (em formato simplificado).
     * @return O Set de {@link MinCategoryDTO}.
     */
    public Set<MinCategoryDTO> getCategories() {
        return categories;
    }

    /**
     * Retorna a quantidade em estoque.
     * @return A quantidade em estoque.
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Define o ID da cerveja.
     * @param id O novo ID.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Compara dois objetos BeerFilterDTO com base no nome e no teor alcoólico.
     * @param o O objeto a ser comparado.
     * @return true se forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeerFilterDTO)) return false;
        BeerFilterDTO that = (BeerFilterDTO) o;
        return Objects.equals(name, that.name)
                && Objects.equals(alcoholContent, that.alcoholContent);
    }

    /**
     * Calcula o hash code com base no nome e no teor alcoólico.
     * @return O hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, alcoholContent);
    }
}