package com.example.steam_clone.service;

import com.example.steam_clone.model.Game;
import com.example.steam_clone.repository.GameRepository;
import com.example.steam_clone.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameService {
    
    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public GameService(GameRepository gameRepository, 
                      CategoryRepository categoryRepository) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Game> searchGames(String title) {
        if (title == null || title.trim().isEmpty()) {
            return getAllGames();
        }
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional(readOnly = true)
    public List<Game> getGamesByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found");
        }
        return gameRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Game> searchGamesByTitleAndCategory(String title, Long categoryId) {
        if (title == null || title.trim().isEmpty()) {
            if (categoryId == null) {
                return getAllGames();
            }
            return getGamesByCategory(categoryId);
        }
        
        if (categoryId == null) {
            return searchGames(title);
        }
        
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found");
        }
        
        return gameRepository.searchGames(title, categoryId);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }
    
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }
    
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
}