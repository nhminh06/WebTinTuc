
package com.news.controller;

import com.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
        if (logout != null) model.addAttribute("logout", "Đã đăng xuất thành công.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             RedirectAttributes ra) {

        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "redirect:/register";
        }
        if (username.length() < 3 || username.length() > 50) {
            ra.addFlashAttribute("error", "Tên đăng nhập phải từ 3-50 ký tự.");
            return "redirect:/register";
        }
        if (userService.existsByUsername(username)) {
            ra.addFlashAttribute("error", "Tên đăng nhập đã tồn tại.");
            return "redirect:/register";
        }
        if (userService.existsByEmail(email)) {
            ra.addFlashAttribute("error", "Email đã được sử dụng.");
            return "redirect:/register";
        }

        userService.register(username, email, password);
        ra.addFlashAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
        return "redirect:/login";
    }
}