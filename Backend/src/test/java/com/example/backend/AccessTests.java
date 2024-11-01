package com.example.backend;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Disabled
class AccessTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void init(){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context).apply(springSecurity())
                .build();
    }
    @Test
    @WithAnonymousUser
    void unableGetAllIfNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"ADMIN"})
    void enableGetAllIfAuthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "user", password = "user", roles = {"USER"})
    void enableGetAllIfAuthorizedUser() throws Exception {
        mockMvc.perform(get("/api/v1/faqs"))
                .andExpect(status().isOk());
    }

}
