package com.oxytoca.app.controller;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.entity.User;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.app.repository.UserRepository;
import com.oxytoca.app.service.MailSendingService;
import com.oxytoca.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
@Sql(value = {"/create-user-before.sql", "/activity-list-before.sql", "/activity-participant-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/activity-list-after.sql", "/create-user-after.sql", "/activity-participant-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ActivitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    UserService userService;
    @Mock
    private MailSendingService mailSendingService;

    @Test
    @WithUserDetails("referee")
    void testGreetingMethod() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("greeting"));
    }


    @Test
    @WithUserDetails("referee")
    void testPosterMethod() throws Exception {
        mockMvc.perform(get("/activitiesPoster"))
                .andExpect(authenticated())
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(3));
    }

    @Test
    @WithUserDetails("referee")
    void testAuthorsActivitiesMethod() throws Exception {
        mockMvc.perform(get("/authorsActivities"))
                .andExpect(authenticated())
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(2));
    }

    @Test
    @WithUserDetails("instructor")
    void testJoiningActivitiesMethod() throws Exception {
        mockMvc.perform(get("/joiningActivities"))
                .andExpect(authenticated())
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(2));
    }

    @Test
    @WithUserDetails("oxyt")
    void testJoinActivity() throws Exception {
        mockMvc.perform(get("/join/155"))
                .andExpect(authenticated())
                .andExpect(view()
                        .name("redirect:/activitiesPoster"));
        mockMvc.perform(get("/joiningActivities"))
                .andExpect(authenticated())
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(3));
    }

    @Test
    @WithUserDetails("oxyt")
    void testDisjoinActivity() throws Exception {
        mockMvc.perform(get("/disjoin/154"))
                .andExpect(authenticated())
                .andExpect(view()
                        .name("redirect:/activitiesPoster"));
        mockMvc.perform(get("/joiningActivities"))
                .andExpect(authenticated())
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(1));
    }

    @Test
    @WithUserDetails("referee")
    void testEditActivity() throws Exception {
        mockMvc.perform(get("/activity/153"))
                .andExpect(status().isOk())
                .andExpect(view().name("activity-edit"))
                .andExpect(model().attributeExists("activity"));
    }

    @Test
    @WithUserDetails("referee")
    void testSaveEditActivity() throws Exception {
        Activity activity = activityRepository.findActivityById(153L);
        MockHttpServletRequestBuilder multipart = multipart("/activity/saveEditActivity/153")
                .file("file", "123".getBytes())
                .param("type", "Турнир")
                .param("text", "bgbgfbg")
                .flashAttr("activity", activity)
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(view()
                        .name("redirect:/authorsActivities"));

        mockMvc.perform(get("/authorsActivities"))
                .andExpect(xpath("//*[@id='activity-list']/div[@data-id='153']/div[2]/p[3]")
                        .string("bgbgfbg"));

    }

    @Test
    @WithUserDetails("referee")
    void testSaveEditEmptyActivity() throws Exception {
        Activity activity = activityRepository.findActivityById(153L);
        String type = activity.getType();
        String text = activity.getText();
        MockHttpServletRequestBuilder multipart = multipart("/activity/saveEditActivity/153")
                .file("file", "123".getBytes())
                .param("type", "")
                .param("text", "")
                .flashAttr("activity", activity)
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(view()
                        .name("redirect:/authorsActivities"));

        mockMvc.perform(get("/authorsActivities"))
                .andExpect(xpath("//*[@id='activity-list']/div[@data-id='153']/div[2]/p[2]")
                        .string(type))
                .andExpect(xpath("//*[@id='activity-list']/div[@data-id='153']/div[2]/p[3]")
                        .string(text));

    }


    @Test
    @WithUserDetails("instructor")
    void testAddNewActivity() throws Exception {
        mockMvc.perform(get("/addNewActivity"))
                .andExpect(status().isOk())
                .andExpect(view().name("activity-form"))
                .andExpect(model().attributeExists("activity"));
    }

    @Test
    @WithUserDetails("instructor")
    void testSaveNewActivity() throws Exception {
        Activity activity = new Activity(2002L, "Выезд", "Выезд", "Воргольские скалы");

        MockHttpServletRequestBuilder multipart = multipart("/saveNewActivity")
                .file("file", "123".getBytes())
                .param("start", "2024-03-25T11:00")
                .param("finish", "2024-03-25T18:00")
                .flashAttr("activity", activity)
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activitiesPoster"));
        mockMvc.perform(get("/activitiesPoster"))
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(4));

    }

    @Test
    @WithUserDetails("instructor")
    void testSaveNewInvalidActivity() throws Exception {
        Activity activity = new Activity(2002L, "Выезд", "", "");

        MockHttpServletRequestBuilder multipart = multipart("/saveNewActivity")
                .file("file", "123".getBytes())
                .param("start", "2024-03-25T11:00")
                .param("finish", "2024-03-25T18:00")
                .flashAttr("activity", activity)
                .with(csrf());

        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(view().name("activity-form"));

    }

    @Test
    @WithUserDetails("referee")
    void testDeleteActivity() throws Exception {
        mockMvc.perform(get("/delete/153"))
                .andExpect(authenticated())
                .andExpect(view()
                        .name("redirect:/authorsActivities"));
        mockMvc.perform(get("/authorsActivities"))
                .andExpect(authenticated())
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(1));
    }

    @Test
    @WithUserDetails("referee")
    void testFilterByText() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/filter")
                .param("text", "Турнир")
                .with(csrf());
        mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(view().name("poster"))
                .andExpect(xpath("//main[@id='activity-list']/div").nodeCount(1));

    }

}
