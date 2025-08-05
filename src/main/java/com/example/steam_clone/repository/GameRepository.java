package com.example.steam_clone.repository;

import com.example.steam_clone.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT g FROM Game g JOIN g.categories c WHERE c.id = :categoryId")
    List<Game> findByCategoryId(@Param("categoryId") Long categoryId);
    
@Query("SELECT g FROM Game g JOIN g.categories c WHERE " +
    "(:title IS NULL OR LOWER(g.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
    "(:categoryId IS NULL OR c.id = :categoryId)")
List<Game> searchGames(
    @Param("title") String title,
    @Param("categoryId") Long categoryId);



}