package com.example.backend.service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.FAQ;
import com.example.backend.model.User;
import com.example.backend.repository.FAQRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FAQService {

    private final FAQRepository faqRepository;
    private final UserRepository userRepository;

    @Autowired
    public FAQService(FAQRepository faqRepository, UserRepository userRepository) {
        this.faqRepository = faqRepository;
        this.userRepository = userRepository;
    }

    public List<FAQ> getFAQs() {
        return faqRepository.findAll();
    }

    public Optional<FAQ> getFAQById(Long id) {

        return faqRepository.findById(id);
    }

    public FAQ addFAQ(FAQ faq) throws ResourceNotFoundException {
        Optional<User> userOptional = userRepository.findById(faq.getUser().getId());

        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("User with id " + faq.getUser().getId() + " not found.");
        }

        faq.setUser(userOptional.get());
        faq.setCreatedAt(new Date());
        faq.setUpdatedAt(new Date());
        return faqRepository.save(faq);
    }

    public void deleteFAQ(Long id) {
        faqRepository.deleteById(id);
    }

    public FAQ updateFAQ(Long id, FAQ updatedFAQ) throws ResourceNotFoundException {
        Optional<FAQ> existingFAQ = faqRepository.findById(id);

        if (!existingFAQ.isPresent()) {
            throw new ResourceNotFoundException("FAQ with id " + id + " not found.");
        }

        FAQ faqToUpdate = existingFAQ.get();

        faqToUpdate.setQuestion(updatedFAQ.getQuestion());
        faqToUpdate.setAnswer(updatedFAQ.getAnswer());
        faqToUpdate.setUpdatedAt(new Date());

        return faqRepository.save(faqToUpdate);
    }
}
