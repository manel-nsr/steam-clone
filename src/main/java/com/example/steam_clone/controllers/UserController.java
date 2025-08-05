// package com.example.steam_clone.controllers;

// import com.example.steam_clone.model.User;
// import com.example.steam_clone.service.UserService;
// import jakarta.validation.Valid;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// @Controller
// public class UserController {

//     @Autowired
//     private UserService userService;

//     // Show registration form
//     @GetMapping("/register")
//     public String showRegistrationForm(Model model) {
//         model.addAttribute("user", new User());
//         return "register";
//     }

//     // Handle registration
//     @PostMapping("/register")
//     public String registerUser(@Valid @ModelAttribute("user") User user, 
//                               BindingResult result, 
//                               Model model,
//                               RedirectAttributes redirectAttributes) {
        
//         // Check for validation errors
//         if (result.hasErrors()) {
//             return "register";
//         }
        
//         // Check if username already exists
//         if (userService.existsByUsername(user.getUsername())) {
//             result.rejectValue("username", "error.user", "Username already exists");
//             return "register";
//         }
        
//         // Check if email already exists
//         if (userService.existsByEmail(user.getEmail())) {
//             result.rejectValue("email", "error.user", "Email already in use");
//             return "register";
//         }
        
//         // Register the user
//         try {
//             userService.registerUser(user);
//             redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
//             return "redirect:/login";
//         } catch (Exception e) {
//             model.addAttribute("error", "Registration failed. Please try again.");
//             return "register";
//         }
//     }

//     // Show login form
//     @GetMapping("/login")
//     public String showLoginForm(@RequestParam(value = "error", required = false) String error,
//                                @RequestParam(value = "logout", required = false) String logout,
//                                Model model) {
//         if (error != null) {
//             model.addAttribute("error", "Invalid username or password");
//         }
//         if (logout != null) {
//             model.addAttribute("success", "You have been logged out successfully");
//         }
//         return "login";
//     }

//     // Show user profile
//     @GetMapping("/profile")
//     public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
//         if (userDetails == null) {
//             return "redirect:/login";
//         }
        
//         User user = userService.findByUsernameOrEmail(userDetails.getUsername())
//                 .orElseThrow(() -> new RuntimeException("User not found"));
        
//         model.addAttribute("user", user);
//         return "profile";
//     }

//     // Delete user account
//     @PostMapping("/delete-account")
//     public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
//                                RedirectAttributes redirectAttributes) {
//         if (userDetails == null) {
//             return "redirect:/login";
//         }
        
//         User user = userService.findByUsernameOrEmail(userDetails.getUsername())
//                 .orElseThrow(() -> new RuntimeException("User not found"));
        
//         userService.deleteUser(user.getId());
//         redirectAttributes.addFlashAttribute("success", "Account deleted successfully");
        
//         return "redirect:/logout";
//     }
    
//     // Home page redirect - redirect to store instead of profile
//     @GetMapping("/")
//     public String home() {
//         return "redirect:/store";
//     }
// }
package com.example.steam_clone.controllers;

import com.example.steam_clone.model.User;
import com.example.steam_clone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle registration
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (result.hasErrors()) {
            return "register";
        }
        
        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already in use");
            return "register";
        }
        
        // Register the user
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "register";
        }
    }

    // Show login form - FIXED VERSION
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully");
        }
        return "login";
    }

    // Show user profile
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/store";

        }
        
        User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        return "profile";
    }

    // Delete user account
    @PostMapping("/delete-account")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        userService.deleteUser(user.getId());
        redirectAttributes.addFlashAttribute("success", "Account deleted successfully");
        
        return "redirect:/logout";
    }
    
    // REMOVED THE PROBLEMATIC HOME REDIRECT - Let your GameController handle /store and /
}