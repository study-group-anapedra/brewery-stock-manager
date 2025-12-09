package com.anapedra.stock_manager.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa a entidade Cerveja (Beer).
 * Esta classe mapeia a tabela "tb_beers" no banco de dados.
 *
 * <p>Contém informações sobre o produto, como nome, teor alcoólico,
 * preço, datas de fabricação/expiração, e mantém o relacionamento
 * com o estoque e as categorias.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "tb_beers")
public class Beer {

    /**
     * O identificador único da cerveja.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome da cerveja. Não pode ser nulo.
     */
    @Column(name = "name")
    private String name;

    /**
     * URL da imagem do produto.
     */
    private String urlImg;

    /**
     * O teor alcoólico da cerveja.
     */
    @Column(name = "alcohol_content", nullable = false)
    private Double alcoholContent;

    /**
     * O preço da cerveja.
     */
    @Column(nullable = false)
    private Double price;


    /**
     * Momento em que a cerveja foi registrada no sistema.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDateTime registrationMoment;

    /**
     * Momento da última atualização do registro da cerveja.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDateTime updateMoment;

    /**
     * Data de fabricação da cerveja.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDate manufactureDate;

    /**
     * Data de expiração/validade da cerveja.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDate expirationDate;

    /**
     * Relacionamento One-to-One com o estoque. A tabela Beer é a proprietária da chave.
     */
    @OneToOne(mappedBy = "beer", cascade = CascadeType.ALL, orphanRemoval = true)    @JoinColumn(name = "stock_id") // <--- ISTO FAZ A TABELA BEER SER A PROPRIETÁRIA
    private Stock stock;

    /**
     * Conjunto de itens de pedido (OrderItem) associados a esta cerveja.
     */
    @OneToMany(mappedBy = "id.beer")
    private Set<OrderItem> items=new HashSet<>();


    /**
     * Conjunto de categorias às quais a cerveja pertence (relacionamento Many-to-Many).
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "beer_category",
            joinColumns = @JoinColumn(name = "beer_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    /**
     * Construtor padrão sem argumentos.
     */
    public Beer() {
    }

    /**
     * Construtor para inicializar todos os campos principais.
     *
     * @param id O identificador da cerveja.
     * @param name O nome da cerveja.
     * @param urlImg A URL da imagem.
     * @param alcoholContent O teor alcoólico.
     * @param price O preço.
     * @param manufactureDate A data de fabricação.
     * @param expirationDate A data de expiração.
     */
    public Beer(Long id, String name, String urlImg, Double alcoholContent, Double price, LocalDate manufactureDate, LocalDate expirationDate) {
        this.id = id;
        this.name = name;
        this.urlImg = urlImg;
        this.alcoholContent = alcoholContent;
        this.price = price;
        this.manufactureDate = manufactureDate;
        this.expirationDate = expirationDate;
        // Os campos de timestamp serão inicializados via @PrePersist/prePersist()
    }

    // --- Helper Methods to maintain Bidirectional relationships (One-to-One and Many-to-Many) ---
    /**
     * Verifica se a cerveja está vencida na data atual.
     * @return true se a data de expiração for anterior ou igual à data de hoje.
     */
    public boolean isExpired() {
        if (this.expirationDate == null) {
            return false; // Se não tem data de validade, considera que não está vencida.
        }
        // isBefore ou isEqual significa que está vencida (ou vence hoje)
        return this.expirationDate.isBefore(LocalDate.now()) || this.expirationDate.isEqual(LocalDate.now());
    }

    /**
     * Define a entidade de estoque associada a esta cerveja.
     * @param stock O objeto Stock a ser associado.
     */
    public void setStock(Stock stock) {
        this.stock = stock;
    }

    /**
     * Retorna a quantidade de cervejas em estoque.
     * Retorna 0 se não houver registro de estoque associado.
     * @return A quantidade em estoque.
     */
    public int returnQuantityStock(){
        int quantity = 0;
        if (this.stock != null) {
            return getStock().getQuantity();
        }
        return quantity;

    }

    /**
     * Retorna o nome da primeira categoria associada (simplificação).
     * @return O nome da primeira categoria encontrada ou " " se não houver categorias.
     */
    public String returnCategoryName(){
        String categoryName = " ";
        for (Category category : categories){
            categoryName = category.getDescription();
        }
        return categoryName;

    }


    /**
     * Adiciona uma categoria ao conjunto de categorias desta cerveja,
     * mantendo a relação bidirecional.
     * @param category A categoria a ser adicionada.
     */
    // For Many-to-Many (Category) - Your methods are already correct for a bidirectional Set
    public void addCategory(Category category) {
        categories.add(category);
        category.getBeers().add(this);
    }

    /**
     * Remove uma categoria do conjunto de categorias desta cerveja,
     * mantendo a relação bidirecional.
     * @param category A categoria a ser removida.
     */
    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBeers().remove(this);
    }



    /**
     * Retorna o ID da cerveja.
     * @return O ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID da cerveja.
     * @param id O novo ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome da cerveja.
     * @return O nome.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome da cerveja.
     * @param name O novo nome.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna a URL da imagem da cerveja.
     * @return A URL da imagem.
     */
    public String getUrlImg() {
        return urlImg;
    }

    /**
     * Define a URL da imagem da cerveja.
     * @param urlImg A nova URL da imagem.
     */
    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    /**
     * Retorna o teor alcoólico da cerveja.
     * @return O teor alcoólico.
     */
    public double getAlcoholContent() {
        return alcoholContent;
    }

    /**
     * Define o teor alcoólico da cerveja.
     * @param alcoholContent O novo teor alcoólico.
     */
    public void setAlcoholContent(Double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    /**
     * Retorna o preço da cerveja.
     * @return O preço.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Define o preço da cerveja.
     * @param price O novo preço.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Retorna o momento do registro da cerveja.
     * @return O momento do registro.
     */
    public LocalDateTime getRegistrationMoment() {
        return registrationMoment;
    }

    /**
     * Define o momento do registro da cerveja.
     * @param registrationMoment O novo momento de registro.
     */
    public void setRegistrationMoment(LocalDateTime registrationMoment) {
        this.registrationMoment = registrationMoment;
    }

    /**
     * Retorna o momento da última atualização do registro da cerveja.
     * @return O momento da atualização.
     */
    public LocalDateTime getUpdateMoment() {
        return updateMoment;
    }

    /**
     * Define o momento da última atualização do registro da cerveja.
     * @param updateMoment O novo momento de atualização.
     */
    public void setUpdateMoment(LocalDateTime updateMoment) {
        this.updateMoment = updateMoment;
    }

    /**
     * Retorna a data de fabricação da cerveja.
     * @return A data de fabricação.
     */
    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    /**
     * Define a data de fabricação da cerveja.
     * @param manufactureDate A nova data de fabricação.
     */
    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    /**
     * Retorna a data de expiração da cerveja.
     * @return A data de expiração.
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Define a data de expiração da cerveja.
     * @param expirationDate A nova data de expiração.
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Retorna a entidade Stock associada a esta cerveja.
     * @return A entidade Stock.
     */
    public Stock getStock() {
        return stock;
    }


    /**
     * Retorna uma lista de pedidos (Order) que contêm esta cerveja,
     * mapeando através dos itens de pedido (OrderItem).
     * @return Uma lista de objetos Order.
     */
    public List<Order> getOrders(){
        return items.stream().map(OrderItem::getOrder).collect(Collectors.toList());
    }


    /**
     * Retorna o conjunto de categorias associadas.
     * @return O Set de Category.
     */
    public Set<Category> getCategories() {
        return categories;
    }

    /**
     * Define o conjunto de categorias associadas.
     * @param categories O novo Set de Category.
     */
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    /**
     * Compara dois objetos Beer com base no ID.
     * @param o O objeto a ser comparado.
     * @return true se os IDs forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Beer beer = (Beer) o;
        return Objects.equals(id, beer.id);
    }

    /**
     * Calcula o hash code com base no ID.
     * @return O hash code do ID.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}