package com.oxytoca.app.controller;

import com.oxytoca.app.entity.User;
import com.oxytoca.app.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * Контроллер действий пользователя с профилем.
 *
 */
@Controller
public class UserController {
    final
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Метод контроллера для отображения HTML-страницы профиля пользователя.
     */
    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Метод контроллера для отображения формы редактирования профиля.
     */
    @GetMapping("/editProfile")
    public String editProfile(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("user", user);
        return "profile-edit";
    }

    /**
     * Метод контроллера для обновления данных после редактирования профиля.
     */
    @PostMapping("/saveProfile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String email,
                                @RequestParam String username,
                                @RequestParam String password) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return "redirect:/profile";
        }
        userService.updateProfile(user, email,
                username, password);
        return "redirect:/profile";
    }
}
