package com.news.controller;

import com.news.model.Article;
import com.news.model.ArticleStatus;
import com.news.model.Report;
import com.news.model.User;
import com.news.service.ArticleService;
import com.news.service.ReportService;
import com.news.service.UserService;
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

    @Autowired private ArticleService articleService;
    @Autowired private UserService userService;
    @Autowired private ReportService reportService;

    @GetMapping
    public String dashboard(Model model) {
        Page<Article> articles = articleService.getAllArticles(0, 10);
        model.addAttribute("articles", articles);
        model.addAttribute("totalArticles", articles.getTotalElements());
        model.addAttribute("totalUsers", userService.countAll());
        model.addAttribute("totalViews", articleService.sumAllViews());
        model.addAttribute("totalPendingReports", reportService.countByStatus(Report.Status.PENDING));
        model.addAttribute("adminPage", "dashboard");
        return "admin/dashboard";
    }
    // ===== ARTICLES =====
    @GetMapping("/articles")
    public String listArticles(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Article> articles = articleService.getAllArticles(page, 11);
        model.addAttribute("articles", articles);
        model.addAttribute("totalArticles", articles.getTotalElements());
        model.addAttribute("totalPublished", articleService.countByStatus(ArticleStatus.PUBLISHED));
        model.addAttribute("totalDraft", articleService.countByStatus(ArticleStatus.DRAFT));
        model.addAttribute("totalHidden", articleService.countByStatus(ArticleStatus.HIDDEN));
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
        if (result.hasErrors()) return "admin/article-form";
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
    public String deleteArticle(@PathVariable Long id, RedirectAttributes ra) {
        articleService.deleteArticle(id);
        ra.addFlashAttribute("success", "Đã xóa bài viết!");
        return "redirect:/admin/articles";
    }

    @GetMapping("/articles/toggle/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes ra) {
        articleService.toggleStatus(id);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái!");
        return "redirect:/admin/articles";
    }

    // ===== USERS =====
    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        Page<User> users = userService.searchUsers(keyword, page, 15);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalUsers", userService.countAll());
        model.addAttribute("totalEnabled", userService.countByEnabled(true));
        model.addAttribute("totalDisabled", userService.countByEnabled(false));
        model.addAttribute("adminPage", "users");
        return "admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id,
                             @RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             RedirectAttributes ra) {
        userService.toggleEnabled(id);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái người dùng!");
        return "redirect:/admin/users?keyword=" + keyword + "&page=" + page;
    }

    @PostMapping("/users/role/{id}")
    public String changeRole(@PathVariable Long id,
                             @RequestParam String role,
                             @RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             RedirectAttributes ra) {
        userService.changeRole(id, role);
        ra.addFlashAttribute("success", "Đã cập nhật vai trò!");
        return "redirect:/admin/users?keyword=" + keyword + "&page=" + page;
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             @RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "Đã xóa người dùng!");
        return "redirect:/admin/users?keyword=" + keyword + "&page=" + page;
    }

    // ===== REPORTS =====
    @GetMapping("/reports")
    public String listReports(@RequestParam(defaultValue = "PENDING") String status,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Report.Status statusEnum = null;
        try { statusEnum = Report.Status.valueOf(status); } catch (Exception ignored) {}

        model.addAttribute("reports", reportService.getByStatus(statusEnum, page, 15));
        model.addAttribute("currentStatus", status);
        model.addAttribute("countPending",   reportService.countByStatus(Report.Status.PENDING));
        model.addAttribute("countResolved",  reportService.countByStatus(Report.Status.RESOLVED));
        model.addAttribute("countDismissed", reportService.countByStatus(Report.Status.DISMISSED));
        model.addAttribute("adminPage", "reports");
        return "admin/reports";
    }

    @PostMapping("/reports/resolve/{id}")
    public String resolveReport(@PathVariable Long id,
                                @RequestParam(defaultValue = "PENDING") String status,
                                @RequestParam(defaultValue = "0") int page,
                                RedirectAttributes ra) {
        reportService.resolveAndDeleteComment(id);
        ra.addFlashAttribute("success", "Đã xử lý: xóa bình luận vi phạm!");
        return "redirect:/admin/reports?status=" + status + "&page=" + page;
    }

    @PostMapping("/reports/dismiss/{id}")
    public String dismissReport(@PathVariable Long id,
                                @RequestParam(defaultValue = "PENDING") String status,
                                @RequestParam(defaultValue = "0") int page,
                                RedirectAttributes ra) {
        reportService.updateStatus(id, Report.Status.DISMISSED);
        ra.addFlashAttribute("success", "Đã bỏ qua báo cáo.");
        return "redirect:/admin/reports?status=" + status + "&page=" + page;
    }

    @PostMapping("/reports/delete/{id}")
    public String deleteReport(@PathVariable Long id,
                               @RequestParam(defaultValue = "PENDING") String status,
                               @RequestParam(defaultValue = "0") int page,
                               RedirectAttributes ra) {
        reportService.deleteReport(id);
        ra.addFlashAttribute("success", "Đã xóa báo cáo.");
        return "redirect:/admin/reports?status=" + status + "&page=" + page;
    }

    // ===== CATEGORIES =====
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("adminPage", "categories");
        return "admin/categories";
    }
}