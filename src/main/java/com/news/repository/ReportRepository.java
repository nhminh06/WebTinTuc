package com.news.repository;

import com.news.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByStatus(Report.Status status, Pageable pageable);
    long countByStatus(Report.Status status);
    boolean existsByCommentIdAndReporterUsername(Long commentId, String username);
}