package com.oxytoca.app.service;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.app.entity.Role;
import com.oxytoca.app.entity.User;
import com.oxytoca.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.UUID;

/**
 * Класс предоставляющий бизнес-логику и общий функционал по работе с объектами класса пользователя.
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final ActivityRepository activityRepository;

    final
    MailSendingService mailSendingService;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ActivityRepository activityRepository, MailSendingService mailSendingService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.mailSendingService = mailSendingService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Метод поиска пользователя по имени пользователя.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    /**
     * Метод, реализующий подписку на мероприятие (добавление в БД информации о подписке)
     */
    @Transactional
    public void joinActivity(Long userId, Activity activity) {
        User user = userRepository.findUserById(userId);
        user.getMyActivities().add(activity);
        userRepository.save(user);
        if(activity.getAuthor().getEmail() != null) {
            sendJoinNotice(activity.getAuthor());
        }
    }

    /**
     * Метод, реализующий отписку от мероприятия (удаление из БД информации о подписке)
     */
    @Transactional
    public void disjoinActivity(Long userId, Long activityId) {
        User user = userRepository.findUserById(userId);
        Activity activity = activityRepository.findActivityById(activityId);

        user.getMyActivities().remove(activity);
        user.removeActivity(activity);
        userRepository.save(user);

        activity.getParticipants().remove(user);
        activityRepository.save(activity);
        if(activity.getAuthor().getEmail() != null) {
            sendDisjoinNotice(activity.getAuthor());
        }
    }

    /**
     * Метод добавления в БД нового пользователя при регистрации
     */
    public boolean addNewUser(User user) {
        User userFromDatabase =
                userRepository.findByUsername(user.getUsername());

        if (userFromDatabase != null) {
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return true;
    }

    /**
     * Метод, посылающий email-сообщение создателю мероприятия
     * о подписке на него нового пользователя
     */
    void sendJoinNotice(User user) {
        if (!user.getEmail().isEmpty()) {
            String message = String.format("Привет, %s! \n" +
                            "Еще один пользователь записался на твое мероприятие! \n" +
                            "К списку твоих мероприятий: " +
                            "http://localhost:8080/authorsActivities",
                    user.getUsername());

            mailSendingService.sendMail(user.getEmail(),
                    "Join to the activity", message);
        }
    }

    /**
     * Метод, посылающий email-сообщение создателю мероприятия
     * об отписке от него пользователя
     */
    void sendDisjoinNotice(User user) {
        if (!user.getEmail().isEmpty()) {
            String message = String.format("Привет, %s! \n" +
                            "Один из пользователей отписался от твоего мероприятия! \n" +
                            "К списку твоих мероприятий: " +
                            "http://localhost:8080/authorsActivities",
                    user.getUsername());

            mailSendingService.sendMail(user.getEmail(),
                    "Disjoin to the activity", message);
        }
    }

    /**
     * Метод, обновляющий в БД информацию о пользователе
     * в результате редактирования профиля
     */
    public void updateProfile(User user, String email,
                              String username, String password) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = !email.equals(userEmail);

        boolean isPasswordChanged = !password.equals(user.getPassword());

        if(isEmailChanged) {
            user.setEmail(email);
        }

        if (!StringUtils.isEmpty(username)) {
            user.setUsername(username);
        }

        if (!StringUtils.isEmpty(password) && isPasswordChanged) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);
    }


}
