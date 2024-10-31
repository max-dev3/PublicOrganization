package com.example.backend.service.faq;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.FAQ;
import com.example.backend.model.User;
import com.example.backend.repository.FAQRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.EntityBuilder;
import com.example.backend.service.FAQService;


public class FAQServiceTest extends EntityBuilder {

    @Mock private FAQRepository faqRepository;
    @Mock private UserRepository userRepository;

    private FAQService faqService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        faqService = new FAQService(faqRepository, userRepository);
    }

    @Test
    void getFAQsTest() {
        var faqList = List.of( getFaqEntity(), getFaqEntity() );
        when( faqRepository.findAll() ).thenReturn( faqList );

        var result = faqService.getFAQs();

        verify( faqRepository, times(1) ).findAll();
        assertEquals(2, result.size());
        assertListEquals(faqList, result);
    }

    @Test
    void getFAQByIdTest() {
        var faq = getFaqEntity();
        when( faqRepository.findById(faq.getId()) ).thenReturn( java.util.Optional.of(faq) );

        var result = faqService.getFAQById(faq.getId());

        verify( faqRepository, times(1) ).findById(faq.getId());
        assertEquals(faq.getId(), result.get().getId());
    }

    @Test
    void addFAQTest() {
        User user = getUserEntity();
        FAQ faq = getFaqEntity();
        faq.setUser(user);
        when( userRepository.findById(faq.getUser().getId()) ).thenReturn( java.util.Optional.of(user) );
        when( faqRepository.save(faq) ).thenReturn(faq);

        FAQ result = assertDoesNotThrow( () -> faqService.addFAQ(faq) );

        verify(userRepository, times(1) ).findById(user.getId());
        verify(faqRepository, times(1)).save(faq);

        assertEquals(faq.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    void addFAQThrowsResourceNotFoundExceptionTest() {
        User user = getUserEntity();
        FAQ faq = getFaqEntity();
        faq.setUser(user);

        when( userRepository.findById(faq.getUser().getId()) ).thenReturn( java.util.Optional.empty() );

        assertThrows(ResourceNotFoundException.class, () -> faqService.addFAQ(faq));
    }

    @Test
    void updateFAQTest() {
        FAQ faq = getFaqEntity();
        FAQ updatedFaq = getFaqEntity();
        when( faqRepository.findById(faq.getId()) ).thenReturn( java.util.Optional.of(faq) );
        when( faqRepository.save(faq) ).thenReturn(faq);

        FAQ result = assertDoesNotThrow( () -> faqService.updateFAQ(faq.getId(), updatedFaq) );

        verify(faqRepository, times(1)).findById(faq.getId());
        verify(faqRepository, times(1)).save(faq);

        assertEquals(result.getId(), faq.getId());
        assertEquals(result.getQuestion(), updatedFaq.getQuestion());
        assertEquals(result.getAnswer(), updatedFaq.getAnswer());
    }

    @Test
    void updateFAQThrowsResourceNotFoundExceptionTest() {
        FAQ faq = getFaqEntity();

        when( faqRepository.findById(faq.getId()) ).thenReturn( java.util.Optional.empty() );

        assertThrows(ResourceNotFoundException.class, () -> faqService.updateFAQ(faq.getId(), faq));
    }

    @Test
    void deleteFAQTest() {
        FAQ faq = getFaqEntity();

        faqService.deleteFAQ(faq.getId());

        verify( faqRepository, times(1) ).deleteById( eq(faq.getId()) );
    }
}
