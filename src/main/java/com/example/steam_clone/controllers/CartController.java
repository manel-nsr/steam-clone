package com.example.steam_clone.controllers;

import com.example.steam_clone.model.Game;
import com.example.steam_clone.model.User;
import com.example.steam_clone.service.CartService;
import com.example.steam_clone.service.GameService;
import com.example.steam_clone.service.PurchaseService;
import com.example.steam_clone.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // ADDED
import java.util.List;
import java.math.BigDecimal;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final GameService gameService;
    private final CartService cartService;
    private final PurchaseService purchaseService;
    private final UserService userService;

    @Autowired
    public CartController(GameService gameService, CartService cartService, 
                         PurchaseService purchaseService, UserService userService) {
        this.gameService = gameService;
        this.cartService = cartService;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Check if user is authenticated
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // Get real user ID
        User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long userId = user.getId();
        
        List<Game> cartGames = cartService.getCartItems(userId).stream()
            .map(cartItem -> cartItem.getGame())
            .toList();

        BigDecimal total = cartGames.stream()
            .map(Game::getFinalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartGames", cartGames);
        model.addAttribute("total", total);
        return "cart/cart";
    }

    // ENHANCED: Added ownership check and flash messages
    @PostMapping("/add")
    public String addToCart(@RequestParam("gameId") Long gameId, 
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) { // ADDED
        // Check if user is authenticated before adding to cart
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to add games to cart"); // ADDED
            return "redirect:/login";
        }
        
        try { // ADDED try-catch
            // Get real user ID
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Long userId = user.getId(); // ADDED
            
            // ADDED: Check if user already owns the game
            if (purchaseService.hasUserPurchasedGame(userId, gameId)) {
                redirectAttributes.addFlashAttribute("warning", 
                        "You already own this game! Check your library.");
                return "redirect:/store/" + gameId;
            }
            
            // ADDED: Check if game is already in cart
            boolean alreadyInCart = cartService.getCartItems(userId).stream()
                    .anyMatch(cartItem -> cartItem.getGame().getId().equals(gameId));
            
            if (alreadyInCart) {
                redirectAttributes.addFlashAttribute("warning", "This game is already in your cart!");
                return "redirect:/cart";
            }
            
            cartService.addToCart(userId, gameId);
            redirectAttributes.addFlashAttribute("success", "Game added to cart!"); // ADDED
            return "redirect:/cart";
            
        } catch (Exception e) { // ADDED
            redirectAttributes.addFlashAttribute("error", "Failed to add to cart: " + e.getMessage());
            return "redirect:/store/" + gameId;
        }
    }

    // ENHANCED: Added flash messages
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long gameId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) { // ADDED
        // Check if user is authenticated
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        try { // ADDED try-catch
            // Get real user ID
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            cartService.removeFromCart(user.getId(), gameId);
            redirectAttributes.addFlashAttribute("success", "Game removed from cart!"); // ADDED
            return "redirect:/cart";
            
        } catch (Exception e) { // ADDED
            redirectAttributes.addFlashAttribute("error", "Failed to remove game: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // ENHANCED: Added ownership checks and flash messages
    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) { // ADDED
        // Check if user is authenticated
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to checkout"); // ADDED
            return "redirect:/login";
        }
        
        try { // ADDED try-catch
            // Get real user ID
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Long userId = user.getId();
            
            // ADDED: Check if cart is empty
            if (cartService.getCartItems(userId).isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", "Your cart is empty!");
                return "redirect:/cart";
            }
            
            // ADDED: Check for already owned games in cart
            long alreadyOwnedCount = cartService.getCartItems(userId).stream()
                    .filter(cartItem -> purchaseService.hasUserPurchasedGame(userId, cartItem.getGame().getId()))
                    .count();

            if (alreadyOwnedCount > 0) {
                redirectAttributes.addFlashAttribute("warning", 
                        "Some games in your cart are already in your library. Please remove them first.");
                return "redirect:/cart";
            }
            
            // Process the checkout
            cartService.getCartItems(userId).forEach(cartItem ->
                purchaseService.savePurchase(userId, cartItem.getGame().getId())
            );
            cartService.checkout(userId);

            redirectAttributes.addFlashAttribute("success", "Purchase successful! Games added to your library."); // ADDED
            return "redirect:/library";
            
        } catch (Exception e) { // ADDED
            redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
            return "redirect:/cart";
        }
    }
    
    // ADDED: New method for adding from game details page
    @PostMapping("/add/{gameId}")
    public String addToCartFromGameDetails(@PathVariable Long gameId, 
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          RedirectAttributes redirectAttributes) {
        // Check if user is authenticated
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to add games to cart");
            return "redirect:/login";
        }
        
        try {
            // Get real user ID
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Long userId = user.getId();
            
            // Check if user already owns the game
            if (purchaseService.hasUserPurchasedGame(userId, gameId)) {
                redirectAttributes.addFlashAttribute("warning", 
                        "You already own this game! Check your library.");
                return "redirect:/store/" + gameId;
            }
            
            // Check if game is already in cart
            boolean alreadyInCart = cartService.getCartItems(userId).stream()
                    .anyMatch(cartItem -> cartItem.getGame().getId().equals(gameId));
            
            if (alreadyInCart) {
                redirectAttributes.addFlashAttribute("warning", "This game is already in your cart!");
                return "redirect:/store/" + gameId;
            }
            
            cartService.addToCart(userId, gameId);
            redirectAttributes.addFlashAttribute("success", "Game added to cart!");
            return "redirect:/cart";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add to cart: " + e.getMessage());
            return "redirect:/store/" + gameId;
        }
    }
}