package com.news.service;

import com.news.model.Article;
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
        return articleRepository.findByPublishedTrueOrderByCreatedAtDesc(pageable);
    }

    public Page<Article> getArticlesByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByCategoryAndPublishedTrueOrderByCreatedAtDesc(category, pageable);
    }

    public List<Article> getTopViewedArticles() {
        return articleRepository.findTop5ByPublishedTrueOrderByViewCountDesc();
    }

    public List<Article> getLatestArticles() {
        return articleRepository.findTop6ByPublishedTrueOrderByCreatedAtDesc();
    }

    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public void incrementView(Long id) {
        articleRepository.incrementViewCount(id);
    }

    public Page<Article> searchArticles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.searchArticles(keyword, pageable);
    }

    public List<Article> getRelatedArticles(String category, Long excludeId) {
        return articleRepository.findTop4ByCategoryAndPublishedTrueAndIdNotOrderByCreatedAtDesc(
            category, excludeId);
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

    public void togglePublish(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setPublished(!article.isPublished());
            articleRepository.save(article);
        });
    }
}
