package com.news.repository;

import com.news.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);
    long countByArticleId(Long articleId);
}