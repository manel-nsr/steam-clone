package com.example.steam_clone.service;

import com.example.steam_clone.model.Game;
import com.example.steam_clone.model.Purchase;
import com.example.steam_clone.repository.GameRepository;
import com.example.steam_clone.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private GameRepository gameRepository;

    /**
     * Save a purchase for a user and game
     */
    @Transactional
    public Purchase savePurchase(Long userId, Long gameId) {
        // Check if already purchased to avoid duplicates
        if (purchaseRepository.existsByUserIdAndGame_Id(userId, gameId)) {
            // FIXED: Use Optional to handle multiple results safely
            Optional<Purchase> existingPurchase = purchaseRepository.findFirstByUserIdAndGame_IdOrderByPurchaseDateDesc(userId, gameId);
            if (existingPurchase.isPresent()) {
                return existingPurchase.get();
            }
        }
        
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));
            
        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setGame(game);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setPurchasePrice(game.getFinalPrice());
        
        return purchaseRepository.save(purchase);
    }
    
    /**
     * Get all games purchased by a user (for library)
     */
    public List<Game> getPurchasedGames(Long userId) {
        return purchaseRepository.findByUserId(userId).stream()
            .map(Purchase::getGame)
            .distinct() // Remove duplicate games in case of duplicate purchases
            .collect(Collectors.toList());
    }
    
    /**
     * Check if user has purchased a specific game
     */
    public boolean hasUserPurchasedGame(Long userId, Long gameId) {
        return purchaseRepository.existsByUserIdAndGame_Id(userId, gameId);
    }
    
    /**
     * Get purchase history for a user
     */
    public List<Purchase> getPurchaseHistory(Long userId) {
        return purchaseRepository.findByUserIdOrderByPurchaseDateDesc(userId);
    }
    
    /**
     * Clean up duplicate purchases (optional utility method)
     */
    @Transactional
    public void cleanupDuplicatePurchases(Long userId, Long gameId) {
        List<Purchase> duplicates = purchaseRepository.findAllByUserIdAndGame_Id(userId, gameId);
        if (duplicates.size() > 1) {
            // Keep the latest purchase, delete the rest
            Purchase latest = duplicates.stream()
                .max((p1, p2) -> p1.getPurchaseDate().compareTo(p2.getPurchaseDate()))
                .orElse(null);
            
            duplicates.stream()
                .filter(p -> !p.equals(latest))
                .forEach(purchaseRepository::delete);
        }
    }
}