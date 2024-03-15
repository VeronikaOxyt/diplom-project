package com.oxytoca.app.controller;

import com.oxytoca.registration.entity.User;
import com.oxytoca.registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("user", user);
        return "profile";
    }
    @GetMapping("/editProfile")
    public String editProfile(@AuthenticationPrincipal User user,
                             Model model) {
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/saveProfile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String email,
                                @RequestParam String username,
                                @RequestParam String password) {
        userService.updateProfile(user, email,
                username, password);
        return "redirect:/profile";
    }
}
