package com.oxytoca.registration.controller;

import com.oxytoca.registration.entity.Role;
import com.oxytoca.registration.entity.User;
import com.oxytoca.registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        System.out.println("----23----");
        return "registration";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@ModelAttribute("user") User user,
                              Model model) {
        System.out.println("----29----");
        if(user == null) {
            return "redirect:/registration";
        }
        System.out.println(user);
        User userFromDatabase =
                userRepository.findByUsername(user.getUsername());

        if (userFromDatabase != null) {
            model.addAttribute("message",
                    "User already exists!");
            return "registration";
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        System.out.println("--------user = " + user);
        return "redirect:/login";
    }
}

