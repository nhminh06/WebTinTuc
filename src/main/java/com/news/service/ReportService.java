package com.news.service;

import com.news.model.Comment;
import com.news.model.Report;
import com.news.repository.CommentRepository;
import com.news.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportService {

    @Autowired private ReportRepository reportRepository;
    @Autowired private CommentRepository commentRepository;

    public String reportComment(Long commentId, String reporterUsername, String reason) {
        if (reportRepository.existsByCommentIdAndReporterUsername(commentId, reporterUsername)) {
            return "duplicate";
        }
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) return "not_found";

        Comment comment = commentOpt.get();
        Report report = new Report();
        report.setCommentId(commentId);
        report.setCommentContent(comment.getContent());
        report.setCommentAuthor(comment.getUsername());
        report.setArticleId(comment.getArticleId());
        report.setReporterUsername(reporterUsername);
        report.setReason(reason);
        reportRepository.save(report);
        return "ok";
    }

    public Page<Report> getByStatus(Report.Status status, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (status == null) return reportRepository.findAll(pr);
        return reportRepository.findByStatus(status, pr);
    }

    public long countByStatus(Report.Status status) {
        return reportRepository.countByStatus(status);
    }

    public void updateStatus(Long id, Report.Status status) {
        reportRepository.findById(id).ifPresent(r -> {
            r.setStatus(status);
            reportRepository.save(r);
        });
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    // Xử lý: xóa comment + đánh dấu report resolved
    public void resolveAndDeleteComment(Long reportId) {
        reportRepository.findById(reportId).ifPresent(r -> {
            commentRepository.deleteById(r.getCommentId());
            // Đánh tất cả report cùng commentId là resolved
            reportRepository.findAll().stream()
                    .filter(rep -> rep.getCommentId().equals(r.getCommentId()))
                    .forEach(rep -> {
                        rep.setStatus(Report.Status.RESOLVED);
                        reportRepository.save(rep);
                    });
        });
    }
}