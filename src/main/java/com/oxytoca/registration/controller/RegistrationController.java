package com.oxytoca.registration.controller;

import com.oxytoca.registration.entity.User;
import com.oxytoca.registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@ModelAttribute("user") User user,
                              Model model) {
        if(user == null) {
            return "redirect:/registration";
        }
        if (!userService.addNewUser(user)) {
            model.addAttribute("message",
                    "User already exists!");
            return "registration";
        }
        System.out.println("redirect login 36");
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activateAccount(Model model,
                                  @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("message",
                    "User successfully activated");
        } else {
            model.addAttribute("message",
                    "Activation code is not found");
        }
        return "login";

    }
}

