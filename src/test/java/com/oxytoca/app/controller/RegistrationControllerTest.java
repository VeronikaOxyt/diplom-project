package com.oxytoca.app.controller;


import com.oxytoca.app.entity.User;
import com.oxytoca.app.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
@Sql(value = {"/create-user-before.sql", "/activity-list-before.sql", "/activity-participant-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/activity-list-after.sql", "/create-user-after.sql", "/activity-participant-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    RegistrationController registrationController;

    @Test
    void testRegistration() throws Exception {
        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    void testAddNewUser() throws Exception {
        User user = new User(1001L, "TestUser", "123",
                null, null, false);
        user.setEmail("nikki@gmail.com");
        MockHttpServletRequestBuilder multipart = multipart("/addNewUser")
                .flashAttr("user", user)
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    void testAddNewInvalidUser() throws Exception {
        User user = new User(1001L, "", "123",
                null, null, false);
        user.setEmail("nikki@gmail.com");
        MockHttpServletRequestBuilder multipart = multipart("/addNewUser")
                .flashAttr("user", user)
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(view().name("registration"));
    }

}
