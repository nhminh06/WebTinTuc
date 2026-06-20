package com.news.service;

import com.news.model.User;
import com.news.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .disabled(!user.isEnabled())
                .build();
    }

    // Thêm method này
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));
    }

    public User register(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        return userRepository.save(user);
    }
    public Page<User> searchUsers(String keyword, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (keyword == null || keyword.isBlank()) {
            return userRepository.findAll(pr);
        }
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pr);
    }

    public long countAll() {
        return userRepository.count();
    }

    public long countByEnabled(boolean enabled) {
        return userRepository.countByEnabled(enabled);
    }

    public void changeRole(Long id, String role) {
        userRepository.findById(id).ifPresent(u -> {
            u.setRole(role);
            userRepository.save(u);
        });
    }
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void toggleEnabled(Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setEnabled(!u.isEnabled());
            userRepository.save(u);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    public void updateProfile(String username, String email, String newPassword, String confirmPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Kiểm tra email trùng
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        user.setEmail(email);

        // Đổi mật khẩu nếu có nhập
        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp!");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }
    public void updateProfile(String currentUsername, String newUsername, String email,
                              String newPassword, String confirmPassword) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Kiểm tra username trùng
        if (!currentUsername.equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        user.setUsername(newUsername);

        // Kiểm tra email trùng
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        user.setEmail(email);

        // Đổi mật khẩu nếu có nhập
        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp!");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }
}
