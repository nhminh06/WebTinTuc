package com.news.service;

import com.news.model.Contact;
import com.news.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public void send(String username, String subject, String message) {
        Contact c = new Contact();
        c.setSenderUsername(username);
        c.setSubject(subject);
        c.setMessage(message);
        contactRepository.save(c);
    }

    public Page<Contact> getAll(Contact.Status status, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (status == null) return contactRepository.findAll(pr);
        return contactRepository.findByStatus(status, pr);
    }

    public List<Contact> getByUser(String username) {
        return contactRepository.findBySenderUsernameOrderByCreatedAtDesc(username);
    }

    public void reply(Long id, String reply) {
        contactRepository.findById(id).ifPresent(c -> {
            c.setReply(reply);
            c.setStatus(Contact.Status.REPLIED);
            c.setRepliedAt(LocalDateTime.now());
            contactRepository.save(c);
        });
    }

    public void delete(Long id) {
        contactRepository.deleteById(id);
    }

    public long countPending() {
        return contactRepository.countByStatus(Contact.Status.PENDING);
    }
}