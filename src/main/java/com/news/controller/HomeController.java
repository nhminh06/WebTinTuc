package com.news.controller;

import com.news.model.Article;
import com.news.model.Comment;
import com.news.model.User;
import com.news.repository.CommentLikeRepository;
import com.news.service.ArticleService;
import com.news.service.CommentService;
import com.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.news.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
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
    public String viewArticle(@PathVariable Long id, Model model,
                              org.springframework.security.core.Authentication authentication) {
        Optional<Article> optArticle = articleService.getArticleById(id);
        if (optArticle.isEmpty()) {
            return "redirect:/";
        }
        Article article = optArticle.get();

        boolean isOwner = authentication != null && authentication.isAuthenticated()
                && authentication.getName().equals(article.getAuthor());
        boolean isAdmin = authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!article.isPublished() && !isOwner && !isAdmin) {
            return "redirect:/";
        }

        if (article.isPublished()) {
            articleService.incrementView(id);
        }

        String currentUsername = authentication != null && authentication.isAuthenticated()
                ? authentication.getName() : null;

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUsername", currentUsername);

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

        // Bình luận
        List<Comment> comments = commentService.getCommentsByArticle(id);

        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, Boolean> likedByMe = new HashMap<>();
        for (Comment c : comments) {
            likeCounts.put(c.getId(), commentLikeRepository.countByCommentId(c.getId()));
            likedByMe.put(c.getId(), currentUsername != null &&
                    commentLikeRepository.existsByCommentIdAndUsername(c.getId(), currentUsername));
        }

        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("likedByMe", likedByMe);
        // Build map username -> avatarUrl cho các comment
        List<String> usernames = comments.stream()
                .map(Comment::getUsername)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> commentAvatars = userService.findByUsernames(usernames)
                .stream()
                .collect(Collectors.toMap(
                        User::getUsername,
                        u -> u.getAvatarUrl() != null ? u.getAvatarUrl() : ""
                ));

        model.addAttribute("commentAvatars", commentAvatars);
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