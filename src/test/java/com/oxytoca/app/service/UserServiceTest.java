package com.oxytoca.app.service;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @Mock
    private MailSendingService mailSendingService;

    @InjectMocks
    private UserService userService;

    @Test
    void testSendJoinNotice() {

        User user = new User();
        user.setUsername("TestUser");
        user.setEmail("test@example.com");

        userService.sendJoinNotice(user);

        String expectedMessage = "Привет, TestUser! \n" +
                "Еще один пользователь записался на твое мероприятие! \n" +
                "К списку твоих мероприятий: http://localhost:8080/authorsActivities";
        verify(mailSendingService).sendMail("test@example.com", "Join to the activity", expectedMessage);
    }

    @Test
    void testSendDisjoinNotice() {

        User user = new User();
        user.setUsername("TestUser");
        user.setEmail("test@example.com");

        userService.sendDisjoinNotice(user);

        String expectedMessage = "Привет, TestUser! \n" +
                "Один из пользователей отписался от твоего мероприятия! \n" +
                "К списку твоих мероприятий: " +
                "http://localhost:8080/authorsActivities";
        verify(mailSendingService).sendMail("test@example.com", "Disjoin to the activity", expectedMessage);
    }

    @Test
    void  testAddNewUserExist() {
       // User user = u
    }

}
