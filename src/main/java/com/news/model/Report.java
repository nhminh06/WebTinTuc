package com.news.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    public enum Status { PENDING, RESOLVED, DISMISSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // người bị báo cáo comment
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    // nội dung comment tại thời điểm report (snapshot)
    @Column(name = "comment_content", columnDefinition = "TEXT")
    private String commentContent;

    @Column(name = "comment_author", length = 50)
    private String commentAuthor;

    @Column(name = "article_id")
    private Long articleId;

    // người gửi báo cáo
    @Column(name = "reporter_username", length = 50, nullable = false)
    private String reporterUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @Column(length = 200)
    private String reason;

    // getter/setter
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public String getCommentContent() { return commentContent; }
    public void setCommentContent(String commentContent) { this.commentContent = commentContent; }
    public String getCommentAuthor() { return commentAuthor; }
    public void setCommentAuthor(String commentAuthor) { this.commentAuthor = commentAuthor; }
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}