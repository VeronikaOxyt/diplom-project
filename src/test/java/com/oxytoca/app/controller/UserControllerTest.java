package com.oxytoca.app.controller;

import com.oxytoca.app.entity.User;
import com.oxytoca.app.repository.UserRepository;
import com.oxytoca.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
@Sql(value = {"/create-user-before.sql", "/activity-list-before.sql", "/activity-participant-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/activity-list-after.sql", "/create-user-after.sql", "/activity-participant-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Test
    @WithUserDetails("referee")
    void testProfileMethod() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("profile"));
    }

    @Test
    @WithUserDetails("referee")
    void testEditProfileMethod() throws Exception {
        mockMvc.perform(get("/editProfile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("profile-edit"));
    }

    @Test
    @WithUserDetails("referee")
    void testUpdateProfileMethod() throws Exception {
        User user = userRepository.findByUsername("referee");
        MockHttpServletRequestBuilder multipart = multipart("/saveProfile")
                .param("email", "nikki@gmail.com")
                .param("username", "referee1")
                .param("password", "tyyt")
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(view()
                        .name("redirect:/profile"));

    }

    @Test
    @WithUserDetails("referee")
    void testUpdateProfileEmptyMethod() throws Exception {
        User user = userRepository.findByUsername("referee");
        MockHttpServletRequestBuilder multipart = multipart("/saveProfile")
                .param("email", "")
                .param("username", "referee1")
                .param("password", "tyyt")
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(view()
                        .name("redirect:/profile"));

    }

}
