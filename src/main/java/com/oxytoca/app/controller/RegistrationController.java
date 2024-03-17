package com.oxytoca.app.controller;

import com.oxytoca.app.entity.User;
import com.oxytoca.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;


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
    public String addNewUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ValidationController.getErrors(bindingResult);
            model.addAttribute("errors", errors);
            return "registration";
        } else if (!userService.addNewUser(user)) {
            model.addAttribute("message",
                    "User already exists!");
            return "registration";
        }


        return "redirect:/login";
    }

}

