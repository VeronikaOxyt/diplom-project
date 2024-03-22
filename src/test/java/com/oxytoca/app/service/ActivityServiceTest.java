package com.oxytoca.app.service;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
@Sql(value = {"/create-user-before.sql", "/activity-list-before.sql", "/activity-participant-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/activity-list-after.sql", "/create-user-after.sql", "/activity-participant-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ActivityServiceTest {
    @Autowired
    private ActivityService activityService;

    @Autowired
    ActivityRepository activityRepository;
    @Mock
    File savedFile;

    @Mock
    private UserService userService;
    @Test
    void testSaveActivityImg() throws Exception {
        Activity activity = activityRepository.findActivityById(153L);
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        activityService.saveActivityImg(file, activity);

        savedFile = new File(activityService.getUploadPath(), activity.getFilename());
        assertTrue(savedFile.exists());

        assertNotNull(activity.getFilename());
        (file).transferTo(savedFile);
    }

    @Test
    void testDeleteActivity() {
        Activity activity = activityRepository.findActivityById(153L);
        long startedCount = activityRepository.count();
        activityService.deleteActivity(activity);
        long finishedCount = activityRepository.count();
        assertFalse(activityRepository.existsById(153L));
        assertEquals(startedCount - 1, finishedCount);
    }
}
