
// package com.example.steam_clone.controllers;

// import com.example.steam_clone.model.User;
// import com.example.steam_clone.service.PurchaseService;
// import com.example.steam_clone.service.CartService;
// import com.example.steam_clone.service.UserService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// @Controller
// @RequestMapping("/purchase")
// public class PurchaseController {

//     @Autowired
//     private PurchaseService purchaseService;
//     @Autowired
//     private CartService cartService;
//     @Autowired
//     private UserService userService;

//     // NEW: Purchase individual game directly
//     @PostMapping("/game/{gameId}")
//     public String purchaseGame(@PathVariable Long gameId, 
//                              @AuthenticationPrincipal UserDetails userDetails,
//                              RedirectAttributes redirectAttributes) {
        
//         // Check if user is authenticated
//         if (userDetails == null) {
//             redirectAttributes.addFlashAttribute("error", "Please login to purchase games");
//             return "redirect:/login";
//         }

//         try {
//             // Get real user ID from authenticated user
//             User user = userService.findByUsernameOrEmail(userDetails.getUsername())
//                     .orElseThrow(() -> new RuntimeException("User not found"));

//             Long userId = user.getId();

//             // Check if user already owns the game
//             if (purchaseService.hasUserPurchasedGame(userId, gameId)) {
//                 redirectAttributes.addFlashAttribute("warning", "You already own this game! Check your library.");
//                 return "redirect:/games/" + gameId; // Redirect back to game details
//             }

//             // Purchase the game
//             purchaseService.savePurchase(userId, gameId);
//             redirectAttributes.addFlashAttribute("success", "Game successfully purchased and added to your library!");
            
//             return "redirect:/library"; // Redirect to library to show the purchased game

//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", "Purchase failed: " + e.getMessage());
//             return "redirect:/games/" + gameId;
//         }
//     }

//     // Finaliser l'achat du panier
//     @PostMapping("/checkout")
//     public String checkout(@AuthenticationPrincipal UserDetails userDetails,
//                           RedirectAttributes redirectAttributes) {
        
//         // Check if user is authenticated
//         if (userDetails == null) {
//             redirectAttributes.addFlashAttribute("error", "Please login to checkout");
//             return "redirect:/login";
//         }

//         try {
//             // Get real user ID from authenticated user
//             User user = userService.findByUsernameOrEmail(userDetails.getUsername())
//                     .orElseThrow(() -> new RuntimeException("User not found"));

//             Long userId = user.getId();

//             // Check if cart is empty
//             if (cartService.getCartItems(userId).isEmpty()) {
//                 redirectAttributes.addFlashAttribute("warning", "Your cart is empty!");
//                 return "redirect:/cart";
//             }

//             // 1. Check for already owned games in cart
//             long alreadyOwnedCount = cartService.getCartItems(userId).stream()
//                     .mapToLong(cartItem -> 
//                             purchaseService.hasUserPurchasedGame(userId, cartItem.getGame().getId()) ? 1 : 0)
//                     .sum();

//             if (alreadyOwnedCount > 0) {
//                 redirectAttributes.addFlashAttribute("warning", 
//                         "Some games in your cart are already in your library. Please remove them first.");
//                 return "redirect:/cart";
//             }

//             // 2. Sauvegarder tous les jeux du panier comme achats
//             cartService.getCartItems(userId).forEach(cartItem ->
//                     purchaseService.savePurchase(userId, cartItem.getGame().getId())
//             );

//             // 3. Vider le panier
//             cartService.checkout(userId);

//             redirectAttributes.addFlashAttribute("success", "Purchase successful! Games added to your library.");
//             return "redirect:/library"; // Redirige vers la bibliothèque

//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
//             return "redirect:/cart";
//         }
//     }
// }
package com.example.steam_clone.controllers;

import com.example.steam_clone.model.User;
import com.example.steam_clone.service.PurchaseService;
import com.example.steam_clone.service.CartService;
import com.example.steam_clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    // ✅ Purchase individual game directly
    @PostMapping("/game/{gameId}")
    public String purchaseGame(@PathVariable Long gameId, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        
        // Check if user is authenticated
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to purchase games");
            return "redirect:/login";
        }

        try {
            // Get real user ID from authenticated user
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long userId = user.getId();

            // ✅ Check if user already owns the game
            if (purchaseService.hasUserPurchasedGame(userId, gameId)) {
                redirectAttributes.addFlashAttribute("warning",
                        "You already own this game! Check your library.");
                return "redirect:/store/" + gameId; // Redirect back to game details
            }

            // Purchase the game
            purchaseService.savePurchase(userId, gameId);
            redirectAttributes.addFlashAttribute("success",
                    "Game successfully purchased and added to your library!");
            
            return "redirect:/library"; // Redirect to library to show the purchased game

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Purchase failed: " + e.getMessage());
            return "redirect:/store/" + gameId;
        }
    }

    // ✅ Finaliser l'achat du panier
    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        
        // Check if user is authenticated
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to checkout");
            return "redirect:/login";
        }

        try {
            // Get real user ID from authenticated user
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long userId = user.getId();

            // Check if cart is empty
            if (cartService.getCartItems(userId).isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", "Your cart is empty!");
                return "redirect:/cart";
            }

            // ✅ Check for already owned games in cart
            long alreadyOwnedCount = cartService.getCartItems(userId).stream()
                    .filter(cartItem -> purchaseService.hasUserPurchasedGame(userId, cartItem.getGame().getId()))
                    .count();

            if (alreadyOwnedCount > 0) {
                redirectAttributes.addFlashAttribute("warning", 
                        "Some games in your cart are already in your library. Please remove them first.");
                return "redirect:/cart";
            }

            // ✅ Save all games from cart as purchases
            cartService.getCartItems(userId).forEach(cartItem ->
                    purchaseService.savePurchase(userId, cartItem.getGame().getId())
            );

            // ✅ Empty the cart
            cartService.checkout(userId);

            redirectAttributes.addFlashAttribute("success", "Purchase successful! Games added to your library.");
            return "redirect:/library"; // Redirige vers la bibliothèque

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
            return "redirect:/cart";
        }
    }
}
