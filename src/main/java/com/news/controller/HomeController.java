package com.news.controller;

import com.news.model.Article;
import com.news.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/")
    public String home(Model model) {
        List<Article> latest = articleService.getLatestArticles();
        List<Article> popular = articleService.getTopViewedArticles();
        model.addAttribute("latestArticles", latest);
        model.addAttribute("popularArticles", popular);
        model.addAttribute("currentPage", "home");
        return "index";
    }

    @GetMapping("/article/{id}")
    public String viewArticle(@PathVariable Long id, Model model) {
        Optional<Article> optArticle = articleService.getArticleById(id);
        if (optArticle.isEmpty() || !optArticle.get().isPublished()) {
            return "redirect:/";
        }
        Article article = optArticle.get();
        articleService.incrementView(id);

        // Parse JSON content thành blocks
        List<Object> contentBlocks = new java.util.ArrayList<>();
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode arr =
                    mapper.readTree(article.getContent());
            if (arr.isArray()) {
                arr.forEach(node ->
                        contentBlocks.add(mapper.convertValue(node, Object.class)));
            }
        } catch (Exception e) {
            // content cũ không phải JSON
            contentBlocks.add(java.util.Map.of(
                    "type", "text",
                    "heading", "",
                    "content", article.getContent() != null ? article.getContent() : ""
            ));
        }

        List<Article> related = articleService.getRelatedArticles(article.getCategory(), id);
        List<Article> popular = articleService.getTopViewedArticles();

        model.addAttribute("article", article);
        model.addAttribute("contentBlocks", contentBlocks);
        model.addAttribute("relatedArticles", related);
        model.addAttribute("popularArticles", popular);
        return "article";
    }

    @GetMapping("/category/{category}")
    public String byCategory(@PathVariable String category,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Page<Article> articles = articleService.getArticlesByCategory(category, page, 9);
        model.addAttribute("articles", articles);
        model.addAttribute("category", category);
        model.addAttribute("currentPage", category);
        return "category";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        if (keyword != null && !keyword.isBlank()) {
            Page<Article> results = articleService.searchArticles(keyword.trim(), page, 9);
            model.addAttribute("articles", results);
            model.addAttribute("keyword", keyword);
        }
        return "search";
    }
}
