package com.example.steam_clone.util;

import com.example.steam_clone.model.Category;
import com.example.steam_clone.model.Game;
import com.example.steam_clone.repository.CategoryRepository;
import com.example.steam_clone.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataImporter implements CommandLineRunner {

    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public DataImporter(GameRepository gameRepository, CategoryRepository categoryRepository) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (gameRepository.count() > 0) {
            System.out.println("Database already contains games. Skipping import.");
            return;
        }

        // Create categories first
        Map<String, Category> categories = createCategories();
        System.out.println("Created " + categories.size() + " categories");

        // Path to your CSV file in resources
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/data/vgsales.csv")))) {
            
            String line;
            boolean headerSkipped = false;
            int importCount = 0;
            int maxImport = 100; // Limit to prevent importing too many games
            
            while ((line = br.readLine()) != null && importCount < maxImport) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                
                // Split the CSV line - Be careful with commas in quoted strings
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length < 5) {
                    System.out.println("Skipping incomplete row: " + line);
                    continue;
                }
                
                try {
                    // Parse data
                    String title = data[1].trim().replace("\"", "");
                    String platform = data[2].trim();
                    
                    int year = 0;
                    if (data.length > 3 && !data[3].trim().isEmpty()) {
                        try {
                            year = Integer.parseInt(data[3].trim());
                        } catch (NumberFormatException e) {
                            year = LocalDate.now().getYear() - 5;
                        }
                    } else {
                        year = LocalDate.now().getYear() - 5;
                    }
                    
                    String genre = "Action"; // Default genre
                    if (data.length > 4 && !data[4].trim().isEmpty()) {
                        genre = data[4].trim().replace("\"", "");
                    }
                    
                    String publisher = "Unknown";
                    if (data.length > 5 && !data[5].trim().isEmpty()) {
                        publisher = data[5].trim().replace("\"", "");
                    }
                    
                    // Create random price between $10 and $60, with 2 decimal places
                    double randomPrice = 10 + (Math.random() * 50);
                    BigDecimal price = new BigDecimal(randomPrice).setScale(2, RoundingMode.HALF_UP);
                    
                    // Create random discount 0%, 10%, 25%, 50%, or 75%
                    int[] discounts = {0, 10, 25, 50, 75};
                    int discount = discounts[new Random().nextInt(discounts.length)];
                    
                    // Generate a random rating between 1.0 and 5.0
                    double randomRating = 1.0 + (Math.random() * 4.0); // Range: 1.0 to 5.0
                    Double rating = randomRating;

                    // Select image based on genre or use placeholder
                    String imageUrl = "/images/games/placeholder.png";
                    if (genre.equalsIgnoreCase("Action") || 
                        genre.equalsIgnoreCase("Shooter") || 
                        genre.equalsIgnoreCase("Fighting")) {
                        imageUrl = "/images/games/action.jpg";
                    } else if (genre.equalsIgnoreCase("RPG") || 
                              genre.equalsIgnoreCase("Role-Playing")) {
                        imageUrl = "/images/games/rpg.jpg";
                    } else if (genre.equalsIgnoreCase("Sports") || 
                              genre.equalsIgnoreCase("Racing")) {
                        imageUrl = "/images/games/sports.jpg";
                    } else if (genre.equalsIgnoreCase("Strategy") || 
                              genre.equalsIgnoreCase("Puzzle")) {
                        imageUrl = "/images/games/strategy.jpg";
                    }
                    
                    // Create game
                    Game game = new Game();
                    game.setTitle(title);
                    game.setDescription("Experience the excitement of " + title + 
                                       ", originally released for " + platform + 
                                       " in " + year + ". Developed by " + publisher + 
                                       ", this " + genre.toLowerCase() + 
                                       " game offers hours of entertainment.");
                    game.setPublisher(publisher);
                    game.setDeveloper(publisher); // Use publisher as developer
                    
                    // Set release date (use January 1st of the release year)
                    if (year > 0) {
                        game.setReleaseDate(LocalDate.of(year, 1, 1));
                    } else {
                        game.setReleaseDate(LocalDate.now().minusYears(5));
                    }
                    
                    game.setPrice(price);
                    game.setDiscount(discount);
                    game.setImageUrl(imageUrl);
                    game.setSystemRequirements("Windows 10, 8GB RAM, Graphics card with DirectX 11 support");
                    game.setRating(rating); // Set the rating

                    // FIXED: Associate category with game BEFORE saving
                    if (categories.containsKey(genre)) {
                        Set<Category> gameCategories = new HashSet<>();
                        gameCategories.add(categories.get(genre));
                        game.setCategories(gameCategories);
                        System.out.println("Associated game '" + title + "' with category '" + genre + "'");
                    }

                    // Save game with its categories
                    Game savedGame = gameRepository.save(game);
                    
                    importCount++;
                    if (importCount % 10 == 0) {
                        System.out.println("Imported " + importCount + " games so far");
                    }
                } catch (Exception e) {
                    System.err.println("Error processing row: " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
            
            System.out.println("Data import completed successfully! Imported " + importCount + " games.");
            
        } catch (Exception e) {
            System.err.println("Error importing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Map<String, Category> createCategories() {
        List<String> categoryNames = Arrays.asList(
            "Action", "Adventure", "RPG", "Strategy", "Simulation", 
            "Sports", "Racing", "Shooter", "Puzzle", "Platform",
            "Fighting", "Role-Playing", "Misc"
        );
        
        Map<String, Category> categoryMap = new HashMap<>();
        
        for (String name : categoryNames) {
            // Check if category already exists
            Optional<Category> existingCategory = categoryRepository.findByName(name);
            
            if (existingCategory.isPresent()) {
                categoryMap.put(name, existingCategory.get());
            } else {
                Category category = new Category();
                category.setName(name);
                Category savedCategory = categoryRepository.save(category);
                categoryMap.put(name, savedCategory);
            }
        }
        
        return categoryMap;
    }
}