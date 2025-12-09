package com.anapedra.stock_manager.domain.dtos;

import com.anapedra.stock_manager.domain.entities.Beer;
import com.anapedra.stock_manager.domain.entities.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * DTO (Data Transfer Object) usado especificamente para a INSERÇÃO de uma nova
 * entidade {@link Beer} no sistema.
 *
 * <p>Contém anotações de validação Javax/Jakarta Bean Validation ({@code @NotBlank},
 * {@code @NotNull}) para garantir que os dados de entrada do cliente (via requisição HTTP)
 * estejam corretos antes de serem processados.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see StockInputDTO
 * @see CategoryDTO
 * @since 0.0.1-SNAPSHOT
 */
public class BeerInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * O identificador da cerveja. Nulo na inserção, usado para consistência de conversão.
     */
    private Long id;

    /**
     * O nome da cerveja. Não pode ser vazio.
     */
    @NotBlank(message = "O nome é obrigatório")
    private String name;

    /**
     * A URL da imagem da cerveja.
     */
    private String urlImg;

    /**
     * O teor alcoólico da cerveja. Não pode ser nulo.
     */
    @NotNull(message = "O teor alcoólico é obrigatório")
    private Double alcoholContent;

    /**
     * O preço unitário da cerveja. Não pode ser nulo e deve ser positivo ou zero.
     */
    @NotNull(message = "O preço é obrigatório")
    @PositiveOrZero(message = "O preço deve ser positivo")
    private Double price;

    /**
     * A data de fabricação. Não pode ser nula.
     */
    @NotNull(message = "A data de fabricação é obrigatória")
    private LocalDate manufactureDate;

    /**
     * A data de validade (expiração).
     */
    private LocalDate expirationDate;

    /**
     * A lista de categorias associadas à cerveja (usando {@link CategoryDTO}).
     */
    private List<CategoryDTO> categories = new ArrayList<>();

    /**
     * O DTO de entrada do estoque, contendo a quantidade inicial.
     * Não pode ser nulo.
     */
    @NotNull(message = "As informações de estoque são obrigatórias")
    private StockInputDTO stock;

    /**
     * Construtor padrão sem argumentos.
     */
    public BeerInsertDTO() {}

    /**
     * Construtor para inicializar os campos primários e o estoque.
     */
    public BeerInsertDTO(String name, String urlImg, Double alcoholContent,
                         Double price, LocalDate manufactureDate,
                         LocalDate expirationDate, StockInputDTO stock) {
        this.name = name;
        this.urlImg = urlImg;
        this.alcoholContent = alcoholContent;
        this.price = price;
        this.manufactureDate = manufactureDate;
        this.expirationDate = expirationDate;
        this.stock = stock;
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Beer}.
     *
     * @param entity A entidade Beer de origem.
     */
    public BeerInsertDTO(Beer entity) {
        id = entity.getId();
        name = entity.getName();
        urlImg = entity.getUrlImg();
        alcoholContent = entity.getAlcoholContent();
        price = entity.getPrice();
        manufactureDate = entity.getManufactureDate();
        expirationDate = entity.getExpirationDate();
        // Converte a entidade Stock para o DTO StockInputDTO
        stock = new StockInputDTO(entity.getStock());

        // Converte as entidades Category para o DTO CategoryDTO
        entity.getCategories().forEach(category -> this.categories.add(new CategoryDTO(category)));
    }

    /**
     * Construtor que inicializa o DTO a partir de uma entidade {@link Beer} e um Set de {@link Category}.
     * Usado para garantir que as categorias sejam carregadas (se não estiverem no entity).
     *
     * @param entity A entidade Beer de origem.
     * @param categories O Set de Category a ser incluído.
     */
    public BeerInsertDTO(Beer entity, Set<Category> categories) {
        this(entity);
        categories.forEach(category -> this.categories.add(new CategoryDTO(category)));
    }

    // --- Getters e Setters ---

    /**
     * Retorna o ID da cerveja.
     * @return O ID.
     */
    public Long getId() { return id; }

    /**
     * Define o ID da cerveja.
     * @param id O novo ID.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna o nome da cerveja.
     * @return O nome.
     */
    public String getName() { return name; }

    /**
     * Define o nome da cerveja.
     * @param name O novo nome.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Retorna a URL da imagem.
     * @return A URL da imagem.
     */
    public String getUrlImg() { return urlImg; }

    /**
     * Define a URL da imagem.
     * @param urlImg A nova URL da imagem.
     */
    public void setUrlImg(String urlImg) { this.urlImg = urlImg; }

    /**
     * Retorna o teor alcoólico.
     * @return O teor alcoólico.
     */
    public Double getAlcoholContent() { return alcoholContent; }

    /**
     * Define o teor alcoólico.
     * @param alcoholContent O novo teor alcoólico.
     */
    public void setAlcoholContent(Double alcoholContent) { this.alcoholContent = alcoholContent; }

    /**
     * Retorna o preço.
     * @return O preço.
     */
    public Double getPrice() { return price; }

    /**
     * Define o preço.
     * @param price O novo preço.
     */
    public void setPrice(Double price) { this.price = price; }

    /**
     * Retorna a data de fabricação.
     * @return A data de fabricação.
     */
    public LocalDate getManufactureDate() { return manufactureDate; }

    /**
     * Define a data de fabricação.
     * @param manufactureDate A nova data de fabricação.
     */
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    /**
     * Retorna a data de validade.
     * @return A data de validade.
     */
    public LocalDate getExpirationDate() { return expirationDate; }

    /**
     * Define a data de validade.
     * @param expirationDate A nova data de validade.
     */
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    /**
     * Retorna a lista de categorias.
     * @return A lista de {@link CategoryDTO}.
     */
    public List<CategoryDTO> getCategories() { return categories; }

    /**
     * Define a lista de categorias.
     * @param categories A nova lista de {@link CategoryDTO}.
     */
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }

    /**
     * Retorna o DTO de entrada do estoque.
     * @return O {@link StockInputDTO}.
     */
    public StockInputDTO getStock() { return stock; }

    /**
     * Define o DTO de entrada do estoque.
     * @param stock O novo {@link StockInputDTO}.
     */
    public void setStock(StockInputDTO stock) { this.stock = stock; }

    /**
     * Compara dois objetos BeerInsertDTO com base no nome e no teor alcoólico.
     * @param o O objeto a ser comparado.
     * @return true se forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeerInsertDTO)) return false;
        BeerInsertDTO that = (BeerInsertDTO) o;
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