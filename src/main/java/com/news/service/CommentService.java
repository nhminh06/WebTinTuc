package com.news.service;

import com.news.model.Comment;
import com.news.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByArticle(Long articleId) {
        return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);
    }

    public long countByArticle(Long articleId) {
        return commentRepository.countByArticleId(articleId);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}