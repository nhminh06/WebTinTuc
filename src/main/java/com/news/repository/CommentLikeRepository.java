package com.news.repository;

import com.news.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentIdAndUsername(Long commentId, String username);
    long countByCommentId(Long commentId);
    @Transactional
    void deleteByCommentIdAndUsername(Long commentId, String username);
}