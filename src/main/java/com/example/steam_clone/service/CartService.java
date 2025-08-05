// package com.example.steam_clone.service;

// import org.springframework.stereotype.Service;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.example.steam_clone.repository.CartItemRepository;
// import com.example.steam_clone.repository.GameRepository;
// import com.example.steam_clone.model.CartItem;
// import com.example.steam_clone.model.Game;
// import java.util.List;

// @Service
// public class CartService {
    
//     private final CartItemRepository cartItemRepository;
//     private final GameRepository gameRepository;
    
//     @Autowired
//     public CartService(CartItemRepository cartItemRepository, GameRepository gameRepository) {
//         this.cartItemRepository = cartItemRepository;
//         this.gameRepository = gameRepository;
//     }
    
//     public List<CartItem> getCartItems(Long userId) {
//         return cartItemRepository.findByUserId(userId);
//     }
    
//     public void addToCart(Long userId, Long gameId) {
//         // Check if the game is already in cart
//         boolean exists = cartItemRepository.existsByUserIdAndGameId(userId, gameId);
//         if (!exists) {
//             Game game = gameRepository.findById(gameId)
//                 .orElseThrow(() -> new RuntimeException("Game not found"));
            
//             CartItem cartItem = new CartItem();
//             cartItem.setGame(game);
            

            
//             cartItemRepository.save(cartItem);
//         }
//     }
    
//     public void removeFromCart(Long itemId) {
//         cartItemRepository.deleteById(itemId);
//     }
    
//     public void checkout(Long userId) {
    
//         List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
//         cartItemRepository.deleteAll(cartItems);
//     }
// }

package com.example.steam_clone.service;

import com.example.steam_clone.model.CartItem;
import com.example.steam_clone.model.Game;
import com.example.steam_clone.repository.CartItemRepository;
import com.example.steam_clone.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private GameRepository gameRepository;

    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void addToCart(Long userId, Long gameId) {
        // Vérifie si le jeu est déjà dans le panier
        if (!cartItemRepository.existsByUserIdAndGame_Id(userId, gameId)) {
            Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
            
            CartItem cartItem = new CartItem();
            cartItem.setGame(game);
            cartItem.setUserId(userId);
            
            cartItemRepository.save(cartItem);
        }
    }

@Transactional
    public void removeFromCart(Long userId, Long gameId) {
        cartItemRepository.deleteByUserIdAndGame_Id(userId, gameId);
    }
@Transactional

public void checkout(Long userId) {
    // 1. Récupère tous les items du panier de l'utilisateur
    List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

    // 3. Vide le panier (supprime tous les items)
    cartItemRepository.deleteByUserId(userId);
}
}