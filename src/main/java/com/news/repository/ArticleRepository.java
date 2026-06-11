package com.news.repository;

import com.news.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Article> findByCategoryAndPublishedTrueOrderByCreatedAtDesc(String category, Pageable pageable);

    List<Article> findTop5ByPublishedTrueOrderByViewCountDesc();

    List<Article> findTop6ByPublishedTrueOrderByCreatedAtDesc();

    @Query("SELECT a FROM Article a WHERE a.published = true AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchArticles(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);

    List<Article> findTop4ByCategoryAndPublishedTrueAndIdNotOrderByCreatedAtDesc(
        String category, Long id);
}
