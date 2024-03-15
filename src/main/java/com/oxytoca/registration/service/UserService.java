package com.oxytoca.registration.service;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.registration.entity.Role;
import com.oxytoca.registration.entity.User;
import com.oxytoca.registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    @Transactional
    public void joinActivity(Long userId, Activity activity) {
        User user = userRepository.findUserById(userId);
        user.getMyActivities().add(activity);
        userRepository.save(user);
    }
    @Transactional
    public void disjoinActivity(Long userId, Activity activity) {
        User user = userRepository.findUserById(userId);
        user.getMyActivities().remove(activity);
        user.removeActivity(activity);
        userRepository.save(user);

        Activity activity1 = activityRepository.findActivityById(activity.getId());
        activity1.getParticipants().remove(user);
        activityRepository.save(activity1);
    }

    public boolean addNewUser(User user) {
        User userFromDatabase =
                userRepository.findByUsername(user.getUsername());

        if (userFromDatabase != null) {
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);
        sendMessage(user);
        return true;
    }

    private void sendMessage(User user) {
        if (!user.getEmail().isEmpty()) {
            String message = String.format("Hello, %s! \n" +
                            "Welcome to My Diplom App! " +
                            "Please, click on the link: " +
                            "http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode());

            mailSendingService.sendMail(user.getEmail(),
                    "Activation code", message);
        }
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }
        user.setActivationCode(null);
        userRepository.save(user);

        return true;
    }

    public void updateProfile(User user, String email,
                              String username, String password) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if(isEmailChanged) {
            user.setEmail(email);
            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!StringUtils.isEmpty(username)) {
            user.setPassword(username);
        }

        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }

        userRepository.save(user);
        if (isEmailChanged) {
            sendMessage(user);
        }
    }
}
