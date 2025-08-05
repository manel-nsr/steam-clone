package com.example.steam_clone.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String developer;
    private String publisher;
    private LocalDate releaseDate;
    private String imageUrl;
    private BigDecimal price;

    private Integer discount = 0;
    private String systemRequirements;
    private Double rating;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "game_categories",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @Transient
    private BigDecimal discountedPrice;

    public Game() {
    }

    public Game(String title, String description, String developer, String publisher,
                LocalDate releaseDate, String imageUrl, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.developer = developer;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.imageUrl = imageUrl;
        this.price = price;
        calculateDiscountedPrice();
    }

    public BigDecimal getFinalPrice() {
        if (discount == null || discount <= 0 || price == null) {
            return price;
        }
        BigDecimal discountAmount = price.multiply(BigDecimal.valueOf(discount)).divide(BigDecimal.valueOf(100));
        return price.subtract(discountAmount);
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void calculateDiscountedPrice() {
        this.discountedPrice = getFinalPrice();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        calculateDiscountedPrice();
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
        calculateDiscountedPrice();
    }

    public String getSystemRequirements() {
        return systemRequirements;
    }

    public void setSystemRequirements(String systemRequirements) {
        this.systemRequirements = systemRequirements;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }
}
