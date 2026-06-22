package com.news.controller;

import com.news.model.User;
import com.news.service.ContactService;
import com.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String contactPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("contacts",
                contactService.getByUser(userDetails.getUsername()));
        return "user/contact";
    }

    @PostMapping("/send")
    public String send(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam String subject,
                       @RequestParam String message,
                       Model model,
                       RedirectAttributes ra) {
        try {
            if (subject.isBlank() || message.isBlank()) {
                User user = userService.findByUsername(userDetails.getUsername());
                model.addAttribute("user", user);
                model.addAttribute("contacts",
                        contactService.getByUser(userDetails.getUsername()));
                ra.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin!");
                return "redirect:/user/contact";
            }
            contactService.send(userDetails.getUsername(), subject, message);
            ra.addFlashAttribute("success", "Đã gửi tin nhắn đến admin!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/user/contact";
    }
}