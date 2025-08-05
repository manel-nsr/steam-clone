package com.example.steam_clone.repository;

import com.example.steam_clone.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository // FIXED: Added missing space
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByUserId(Long userId);

    List<Purchase> findByUserIdOrderByPurchaseDateDesc(Long userId);

    boolean existsByUserIdAndGame_Id(Long userId, Long gameId);

    // FIXED: Return Optional to handle multiple results safely
    Optional<Purchase> findFirstByUserIdAndGame_IdOrderByPurchaseDateDesc(Long userId, Long gameId);

    // Alternative: Get all purchases for user and game (in case you want to see duplicates)
    List<Purchase> findAllByUserIdAndGame_Id(Long userId, Long gameId);

    // Optional: Custom query to get the latest purchase only
    @Query("SELECT p FROM Purchase p WHERE p.userId = ?1 AND p.game.id = ?2 ORDER BY p.purchaseDate DESC LIMIT 1")
    Optional<Purchase> findLatestPurchaseByUserIdAndGameId(Long userId, Long gameId);
}