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

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    MailSendingService mailSendingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    @Transactional
    public void joinActivity(Long userId, Activity activity) {
        User user = userRepository.findUserById(userId);
        user.getMyActivities().add(activity);
        userRepository.save(user);
        sendJoinNotice(activity.getAuthor());
    }
    @Transactional
    public void disjoinActivity(Long userId, Long activityId) {
        User user = userRepository.findUserById(userId);
        Activity activity = activityRepository.findActivityById(activityId);

        user.getMyActivities().remove(activity);
        user.removeActivity(activity);
        userRepository.save(user);

        activity.getParticipants().remove(user);
        activityRepository.save(activity);
        //sendDisjoinNotice(activity.getAuthor());
    }

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


    private void sendJoinNotice(User user) {
        if (!user.getEmail().isEmpty()) {
            String message = String.format("Привет, %s! \n" +
                            "Еще один пользователь записался на твое мероприятие" +
                            "К списку твоих мероприятий: " +
                            "http://localhost:8080/authorsActivities",
                    user.getUsername());

            mailSendingService.sendMail(user.getEmail(),
                    "Join to the activity", message);
        }
    }

    private void sendDisjoinNotice(User user) {
        if (!user.getEmail().isEmpty()) {
            String message = String.format("Привет, %s! \n" +
                            "Один из пользователей отписался от твоего мероприятия" +
                            "К списку твоих мероприятий: " +
                            "http://localhost:8080/authorsActivities",
                    user.getUsername());

            mailSendingService.sendMail(user.getEmail(),
                    "Join to the activity", message);
        }
    }


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
