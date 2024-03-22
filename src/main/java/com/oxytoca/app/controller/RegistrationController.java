package com.oxytoca.app.controller;

import com.oxytoca.app.entity.User;
import com.oxytoca.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;
/**
 * Контроллер регистрации пользователей.
 *
 */
@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Метод контроллера для отображения HTML-страницы с формой регистрации.
     */
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

