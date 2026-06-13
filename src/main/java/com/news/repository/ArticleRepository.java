package com.news.repository;

import com.news.model.Article;
import com.news.model.ArticleStatus;
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

    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    Page<Article> findByCategoryAndStatusOrderByCreatedAtDesc(String category, ArticleStatus status, Pageable pageable);

    List<Article> findTop5ByStatusOrderByViewCountDesc(ArticleStatus status);

    List<Article> findTop10ByStatusOrderByCreatedAtDesc(ArticleStatus status);

    Page<Article> findByAuthorOrderByCreatedAtDesc(String author, Pageable pageable);

    long countByStatus(ArticleStatus status);
    @Query("SELECT a FROM Article a WHERE a.status = :status AND " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchArticles(@Param("keyword") String keyword,
                                 @Param("status") ArticleStatus status,
                                 Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);

    List<Article> findTop4ByCategoryAndStatusAndIdNotOrderByCreatedAtDesc(
            String category, ArticleStatus status, Long id);


}