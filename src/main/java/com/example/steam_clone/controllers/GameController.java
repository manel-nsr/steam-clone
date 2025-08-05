
package com.example.steam_clone.controllers;

import com.example.steam_clone.model.Game;
import com.example.steam_clone.model.User;
import com.example.steam_clone.service.CategoryService;
import com.example.steam_clone.service.GameService;
import com.example.steam_clone.service.PurchaseService;
import com.example.steam_clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/store")
public class GameController {
    
    private final GameService gameService;
    private final CategoryService categoryService;
    private final PurchaseService purchaseService;
    private final UserService userService;
    
    @Autowired
    public GameController(GameService gameService, CategoryService categoryService, 
                         PurchaseService purchaseService, UserService userService) {
        this.gameService = gameService;
        this.categoryService = categoryService;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }
    
    // Display all games (store homepage)
    @GetMapping
    public String showStore(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "store/index";
    }
    
    // Game details page
    @GetMapping("/{id}")
    public String showGameDetails(@PathVariable("id") Long id,
                                 @RequestParam(required = false) String status,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        try {
            Game game = gameService.getGameById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game ID: " + id));
            
            model.addAttribute("game", game);
            
            // Handle status messages
            if ("success".equals(status)) {
                model.addAttribute("purchaseSuccess", true);
            } else if ("already_owned".equals(status)) {
                model.addAttribute("alreadyOwned", true);
            }
            
            return "store/game-details";
        } catch (Exception e) {
            System.err.println("Error loading game details: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/store?error=game-load-failed";
        }
    }
    
    @GetMapping("/search")
    public String searchGames(@RequestParam(required = false) String title,
                             @RequestParam(required = false) Long categoryId,
                             Model model) {
        try {
            List<Game> games = gameService.searchGamesByTitleAndCategory(title, categoryId);
            model.addAttribute("games", games);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("games", gameService.getAllGames());
        }
        
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("searchTitle", title);
        model.addAttribute("selectedCategoryId", categoryId);
        return "store/index";
    }
    
    // Filter games by category
    @GetMapping("/category/{categoryId}")
    public String filterByCategory(@PathVariable("categoryId") Long categoryId, Model model) {
        List<Game> games = gameService.getGamesByCategory(categoryId);
        model.addAttribute("games", games);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        return "store/index";
    }
    
    // FIXED: Smart purchase logic
    @PostMapping("/{id}/purchase")
    public String purchaseGame(@PathVariable("id") Long id, 
                              @AuthenticationPrincipal UserDetails userDetails) {
        
        // Step 1: Check if user is authenticated
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // Step 2: Get the authenticated user
        User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Step 3: Check if user already owns this game
        if (purchaseService.hasUserPurchasedGame(user.getId(), id)) {
            return "redirect:/store/" + id + "?status=already_owned";
        }
        
        // Step 4: Process the purchase
        try {
            purchaseService.savePurchase(user.getId(), id);
            return "redirect:/store/" + id + "?status=success";
        } catch (Exception e) {
            System.err.println("Error processing purchase: " + e.getMessage());
            return "redirect:/store/" + id + "?error=purchase_failed";
        }
    }
}