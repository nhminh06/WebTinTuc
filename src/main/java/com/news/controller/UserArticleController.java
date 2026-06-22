package com.news.controller;

import com.news.model.Article;
import com.news.model.ArticleStatus;
import com.news.service.ArticleService;
import com.news.model.User;
import com.news.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @GetMapping("/articles")
    public String myArticles(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        Page<Article> articles = articleService.getArticlesByAuthor(userDetails.getUsername(), page, 10);
        model.addAttribute("user", user);
        model.addAttribute("articles", articles);
        model.addAttribute("userPage", "articles");
        return "user/articles";
    }

    @GetMapping("/articles/new")
    public String newArticleForm(@AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("article", new Article());
        model.addAttribute("userPage", "new-article");
        return "user/article-form";
    }

    @PostMapping("/articles/save")
    public String saveArticle(@AuthenticationPrincipal UserDetails userDetails,
                              @Valid @ModelAttribute Article article,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("user", user);
            model.addAttribute("userPage", "new-article");
            return "user/article-form";
        }
        // Tự động set author = username đang đăng nhập
        article.setAuthor(userDetails.getUsername());
        // User chỉ được lưu DRAFT, không được PUBLISHED thẳng
        if (article.getStatus() == null) {
            article.setStatus(ArticleStatus.DRAFT);
        }
        articleService.saveArticle(article);
        redirectAttributes.addFlashAttribute("success", "Lưu bài viết thành công!");
        return "redirect:/user/articles";
    }

    @GetMapping("/articles/edit/{id}")
    public String editArticleForm(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable Long id, Model model) {
        Optional<Article> article = articleService.getArticleById(id);
        if (article.isEmpty()) return "redirect:/user/articles";

        // Chỉ cho sửa bài của chính mình
        if (!article.get().getAuthor().equals(userDetails.getUsername())) {
            return "redirect:/user/articles";
        }

        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("article", article.get());
        model.addAttribute("userPage", "new-article");
        return "user/article-form";
    }

    @GetMapping("/articles/delete/{id}")
    public String deleteArticle(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        Optional<Article> article = articleService.getArticleById(id);
        if (article.isPresent() && article.get().getAuthor().equals(userDetails.getUsername())) {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa bài viết!");
        }
        return "redirect:/user/articles";
    }
}