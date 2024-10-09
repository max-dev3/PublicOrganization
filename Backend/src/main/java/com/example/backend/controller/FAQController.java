package com.example.backend.controller;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.FAQ;
import com.example.backend.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/faqs")
public class FAQController {

    private final FAQService faqService;

    @Autowired
    public FAQController(FAQService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public List<FAQ> getFAQs() {
        return faqService.getFAQs();
    }

    @GetMapping("/{id}")
    public Optional<FAQ> getFAQById(@PathVariable Long id) {
        return faqService.getFAQById(id);
    }

    @PostMapping
    public FAQ addFAQ(@RequestBody FAQ faq) throws ResourceNotFoundException {
        return faqService.addFAQ(faq);
    }
    @PutMapping("/{id}")
    public FAQ updateFAQ(@PathVariable Long id, @RequestBody FAQ faq) throws ResourceNotFoundException {
        return faqService.updateFAQ(id, faq);
    }
    @DeleteMapping("/{id}")
    public void deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
    }
}
