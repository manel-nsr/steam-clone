
// package com.example.steam_clone.controllers;

// import com.example.steam_clone.model.Game;
// import com.example.steam_clone.model.User;
// import com.example.steam_clone.service.GameService;
// import com.example.steam_clone.service.PurchaseService;
// import com.example.steam_clone.service.UserService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import java.util.List;

// @Controller
// @RequestMapping("/library")
// public class LibraryController {
    
//     private final GameService gameService;
//     private final PurchaseService purchaseService;
//     private final UserService userService; // ADD THIS
    
//     @Autowired
//     public LibraryController(GameService gameService, PurchaseService purchaseService, UserService userService) {
//         this.gameService = gameService;
//         this.purchaseService = purchaseService;
//         this.userService = userService; // ADD THIS
//     }
    
//     @GetMapping
//     public String viewLibrary(@AuthenticationPrincipal UserDetails userDetails, Model model) {
//         // FIXED: Check if user is authenticated
//         if (userDetails == null) {
//             return "redirect:/login";
//         }
        
//         // FIXED: Get real user ID from authenticated user
//         User user = userService.findByUsernameOrEmail(userDetails.getUsername())
//                 .orElseThrow(() -> new RuntimeException("User not found"));
        
//         // FIXED: Use real user ID instead of 1L
//         List<Game> userGames = purchaseService.getPurchasedGames(user.getId());
        
//         model.addAttribute("games", userGames);
//         model.addAttribute("user", user); // Add user info for the template
//         return "library/library";
//     }
// }
package com.example.steam_clone.controllers;

import com.example.steam_clone.model.Game;
import com.example.steam_clone.model.User;
import com.example.steam_clone.service.PurchaseService;
import com.example.steam_clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showLibrary(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        
        // Check if user is authenticated
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            // Get real user ID from authenticated user
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long userId = user.getId();

            // Get all purchased games for this user
            List<Game> purchasedGames = purchaseService.getPurchasedGames(userId);

            model.addAttribute("games", purchasedGames);
            model.addAttribute("user", user);
            model.addAttribute("gameCount", purchasedGames.size());

            return "library/library"; // This should be your library.html template

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load library: " + e.getMessage());
            return "library/library";
        }
    }
}