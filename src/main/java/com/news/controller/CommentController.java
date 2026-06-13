package com.news.controller;

import com.news.model.Comment;
import com.news.model.CommentLike;
import com.news.repository.CommentLikeRepository;
import com.news.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @PostMapping("/add/{articleId}")
    public String addComment(@PathVariable Long articleId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             @Valid @ModelAttribute("newComment") Comment comment,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentError", "Bình luận không được để trống hoặc quá dài");
            return "redirect:/article/" + articleId;
        }
        comment.setArticleId(articleId);
        comment.setUsername(userDetails.getUsername());
        commentService.saveComment(comment);
        return "redirect:/article/" + articleId + "#comments";
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        Optional<Comment> comment = commentService.getCommentById(id);
        if (comment.isPresent()) {
            Long articleId = comment.get().getArticleId();
            boolean isOwner = comment.get().getUsername().equals(userDetails.getUsername());
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isOwner || isAdmin) {
                commentService.deleteComment(id);
            }
            return "redirect:/article/" + articleId + "#comments";
        }
        return "redirect:/";
    }

    @PostMapping("/like/{id}")
    public String likeComment(@PathVariable Long id,
                              Authentication authentication,
                              HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        if (commentLikeRepository.existsByCommentIdAndUsername(id, username)) {
            commentLikeRepository.deleteByCommentIdAndUsername(id, username);
        } else {
            CommentLike like = new CommentLike();
            like.setCommentId(id);
            like.setUsername(username);
            commentLikeRepository.save(like);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/report/{id}")
    public String reportComment(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        // TODO: lưu báo cáo vào DB nếu cần
        redirectAttributes.addFlashAttribute("commentError", "Đã gửi báo cáo bình luận.");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}