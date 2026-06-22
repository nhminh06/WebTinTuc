package com.news.repository;

import com.news.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByStatus(Contact.Status status, Pageable pageable);
    List<Contact> findBySenderUsernameOrderByCreatedAtDesc(String username);
    long countByStatus(Contact.Status status);
}