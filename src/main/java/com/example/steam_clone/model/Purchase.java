

// package com.example.steam_clone.model;

// import jakarta.persistence.*;
// import java.math.BigDecimal;
// import java.time.LocalDate;

// @Entity
// @Table(name = "purchases") // Explicit table name (optional)
// public class Purchase {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "user_id", nullable = false)
//     private Long userId;

//     @Column(name = "purchase_date", nullable = false)
//     private LocalDate purchaseDate;

//     @Column(nullable = false, precision = 10, scale = 2)
//     private BigDecimal price;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "game_id", nullable = false)
//     private Game game;

//     // Constructors
//     public Purchase() {
//         this.purchaseDate = LocalDate.now();
//     }

//     public Purchase(Long userId, Game game, BigDecimal price) {
//         this.userId = userId;
//         this.game = game;
//         this.price = price;
//         this.purchaseDate = LocalDate.now();
//     }

//     // Getters
//     public Long getId() { return id; }
//     public Long getUserId() { return userId; }
//     public LocalDate getPurchaseDate() { return purchaseDate; }
//     public BigDecimal getPrice() { return price; }
//     public Game getGame() { return game; }

//     // Setters
//     public void setId(Long id) { this.id = id; }
//     public void setUserId(Long userId) { this.userId = userId; }
//     public void setPurchaseDate(LocalDate purchaseDate) { 
//         this.purchaseDate = purchaseDate; 
//     }
//     public void setPrice(BigDecimal price) { 
//         this.price = price; 
//     }
//     public void setGame(Game game) { 
//         this.game = game; 
//     }

//     // Optional: Helper method
//     public void setPriceFromGame() {
//         if (this.game != null) {
//             this.price = this.game.getFinalPrice();
//         }
//     }
// }
package com.example.steam_clone.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;
    
    @Column(name = "purchase_price", nullable = false)
    private BigDecimal purchasePrice;

    // Getters and setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}