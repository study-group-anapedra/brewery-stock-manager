package com.anapedra.stock_manager.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_beers")
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    
    private String urlImg;
    
    @Column(name = "alcohol_content", nullable = false)
    private Double alcoholContent;
    
    @Column(nullable = false)
    private Double price;


    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDateTime registrationMoment;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDateTime updateMoment;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDate manufactureDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT")
    private LocalDate expirationDate;
/*
    @OneToOne(mappedBy = "beer", cascade = CascadeType.ALL, orphanRemoval = true)    @JoinColumn(name = "stock_id") // <--- ISTO FAZ A TABELA BEER SER A PROPRIETÁRIA
    private Stock stock;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "beer_category", // Name of the join table
        joinColumns = @JoinColumn(name = "beer_id"), // Foreign key column for Beer in the join table
        inverseJoinColumns = @JoinColumn(name = "category_id") // Foreign key column for Category in the join table
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "id.beer")
    private Set<OrderItem> items=new HashSet<>();

 */

    public Beer() {
    }

    // Na classe com.anasantana.bookstore.entities.Beer

    // Construtor completo usado no CommandLineRunner
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
/*
    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int returnQuantityStock(){
        int quantity = 0;
        if (this.stock != null) {
            return getStock().getQuantity();
        }
        return quantity;

    }

    public String returnCategoryName(){
        String categoryName = " ";
        for (Category category : categories){
            categoryName = category.getName();
        }
        return categoryName;

    }



    // For Many-to-Many (Category) - Your methods are already correct for a bidirectional Set
    public void addCategory(Category category) {
        categories.add(category);
        category.getBeers().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBeers().remove(this);
    }

 */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public double getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(Double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getRegistrationMoment() {
        return registrationMoment;
    }

    public void setRegistrationMoment(LocalDateTime registrationMoment) {
        this.registrationMoment = registrationMoment;
    }

    public LocalDateTime getUpdateMoment() {
        return updateMoment;
    }

    public void setUpdateMoment(LocalDateTime updateMoment) {
        this.updateMoment = updateMoment;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
/*
    public Stock getStock() {
        return stock;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public List<Order> getOrders(){
        return items.stream().map(OrderItem::getOrder).collect(Collectors.toList());
    }

 */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Beer beer = (Beer) o;
        return Objects.equals(id, beer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
