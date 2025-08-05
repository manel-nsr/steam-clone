package com.example.steam_clone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @GetMapping("/about")
    public String about() {
        return "about/about"; // Cela cherche un fichier "about.html" dans src/main/resources/templates/
    }
}
