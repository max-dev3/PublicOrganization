package com.example.backend.service.faq;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.FAQRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.DBWriter;
import com.example.backend.service.FAQService;


public class FAQServiceIntegrationTest extends DBWriter {

    @Autowired private FAQService faqService;
    @Autowired private FAQRepository faqRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setup() {
        faqRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    void getFAQsTest() {
        var user = writeUserToDB();
        var faq1 = getFaqEntity(user);
        var faq2 = getFaqEntity(user);
        var faqs = List.of( writeFAQToDB(faq1), writeFAQToDB(faq2) );

        var result = assertDoesNotThrow(() -> faqService.getFAQs() );

        assertListEquals(faqs, result);
    }

    @Test
    void getFAQByIdTest() {
        var user = writeUserToDB();
        var faq = getFaqEntity(user);
        var faqId = writeFAQToDB(faq).getId();

        var result = assertDoesNotThrow( () -> faqService.getFAQById(faqId) );

        assertEquals(faqId, result.get().getId());
    }

    @Test
    void addFAQTest() {
        var user = writeUserToDB();
        var faq = getFaqEntity(user, null);

        var result = assertDoesNotThrow( () -> faqService.addFAQ(faq) );
        
        var faqFromDB = faqRepository.findById(result.getId());
        assertTrue( faqFromDB.isPresent() );
        assertEquals(faqFromDB.get().getId(), result.getId());
    }

    @Test
    void addFAQUserNotFoundTest() {
        var faq = getFaqEntity( getUserEntity(), null );

        var exception = assertThrows(ResourceNotFoundException.class, () -> faqService.addFAQ(faq));
        assertEquals("User with id " + faq.getUser().getId() + " not found.", exception.getMessage());
    }

    @Test
    void deleteFAQTest() {
        var user = writeUserToDB();
        var faq = getFaqEntity(user);
        var faqId = writeFAQToDB(faq).getId();

        assertDoesNotThrow( () -> faqService.deleteFAQ(faqId) );

        var faqFromDB = faqRepository.findById(faqId);
        assertTrue( faqFromDB.isEmpty() );
    }

    @Test
    void updateFAQTest() {
        var user = writeUserToDB();
        var user2 = writeUserToDB();
        var faq = getFaqEntity(user);
        var faqId = writeFAQToDB(faq).getId();

        var expectedFaq = getFaqEntity(user2);
        expectedFaq.setId(faqId);

        var result = assertDoesNotThrow( () -> faqService.updateFAQ(faqId, expectedFaq) );

        assertEquals(expectedFaq.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    void updateFAQNotFoundTest() {
        var faq = getFaqEntity( getUserEntity() );

        var exception = assertThrows(ResourceNotFoundException.class, () -> faqService.updateFAQ(NEW_ENTITY_ID, faq));
        assertEquals("FAQ with id " + NEW_ENTITY_ID + " not found.", exception.getMessage());
    }

    @Override
    protected FAQRepository getFaqRepository() {
        return faqRepository;
    }

    @Override
    protected UserRepository getUserRepository() {
        return userRepository;
    }
}
