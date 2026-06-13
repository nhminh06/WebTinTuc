package com.news.service;

import com.news.model.Article;
import com.news.model.ArticleStatus;
import com.news.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Page<Article> getPublishedArticles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.PUBLISHED, pageable);
    }

    public Page<Article> getArticlesByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, ArticleStatus.PUBLISHED, pageable);
    }

    public List<Article> getTopViewedArticles() {
        return articleRepository.findTop5ByStatusOrderByViewCountDesc(ArticleStatus.PUBLISHED);
    }

    public List<Article> getLatestArticles() {
        return articleRepository.findTop10ByStatusOrderByCreatedAtDesc(ArticleStatus.PUBLISHED);
    }

    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public void incrementView(Long id) {
        articleRepository.incrementViewCount(id);
    }

    public Page<Article> searchArticles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.searchArticles(keyword, ArticleStatus.PUBLISHED, pageable);
    }

    public List<Article> getRelatedArticles(String category, Long excludeId) {
        return articleRepository.findTop4ByCategoryAndStatusAndIdNotOrderByCreatedAtDesc(
                category, ArticleStatus.PUBLISHED, excludeId);
    }

    // Admin methods
    public Page<Article> getAllArticles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findAll(pageable);
    }

    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    public void toggleStatus(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            ArticleStatus next = switch (article.getStatus()) {
                case DRAFT -> ArticleStatus.PUBLISHED;
                case PUBLISHED -> ArticleStatus.HIDDEN;
                case HIDDEN -> ArticleStatus.DRAFT;
            };
            article.setStatus(next);
            articleRepository.save(article);
        });
    }
}