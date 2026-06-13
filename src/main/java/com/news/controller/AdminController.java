package com.news.controller;

import com.news.model.Article;
import com.news.model.ArticleStatus;
import com.news.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public String dashboard(Model model) {
        Page<Article> articles = articleService.getAllArticles(0, 10);
        model.addAttribute("articles", articles);
        model.addAttribute("totalArticles", articles.getTotalElements());
        model.addAttribute("adminPage", "dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/articles")
    public String listArticles(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Article> articles = articleService.getAllArticles(page, 10);
        model.addAttribute("articles", articles);
        model.addAttribute("adminPage", "articles");
        return "admin/articles";
    }

    @GetMapping("/articles/new")
    public String newArticleForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("statuses", ArticleStatus.values());
        model.addAttribute("adminPage", "new-article");
        return "admin/article-form";
    }

    @PostMapping("/articles/save")
    public String saveArticle(@Valid @ModelAttribute Article article,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/article-form";
        }
        articleService.saveArticle(article);
        redirectAttributes.addFlashAttribute("success", "Lưu bài viết thành công!");
        return "redirect:/admin/articles";
    }

    @GetMapping("/articles/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Optional<Article> article = articleService.getArticleById(id);
        if (article.isEmpty()) return "redirect:/admin/articles";
        model.addAttribute("article", article.get());
        model.addAttribute("statuses", ArticleStatus.values());
        model.addAttribute("adminPage", "articles");
        return "admin/article-form";
    }

    @GetMapping("/articles/delete/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.deleteArticle(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa bài viết!");
        return "redirect:/admin/articles";
    }

    @GetMapping("/articles/toggle/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái!");
        return "redirect:/admin/articles";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("adminPage", "users");
        return "admin/users";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("adminPage", "categories");
        return "admin/categories";
    }

    @GetMapping("/reports")
    public String listReports(Model model) {
        model.addAttribute("adminPage", "reports");
        return "admin/reports";
    }
}