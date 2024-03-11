package com.oxytoca.registration.service;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.registration.entity.User;
import com.oxytoca.registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public void joinActivity(Long userId, Activity activity) {
        System.out.println("in userService");
        System.out.println("user " + userId);
        System.out.println("activity " + activity.getId());
        User user = userRepository.findUserById(userId);
        user.getMyActivities().add(activity);
        userRepository.save(user);
    }

    public void disjoinActivity(Long userId, Activity activity) {
        System.out.println("in userService disjoinActivity");
        User user = userRepository.findUserById(userId);
        user.getMyActivities().remove(activity);

        user.removeActivity(activity);
        userRepository.save(user);
    }
}
