package com.oxytoca.app.controller;

import com.oxytoca.app.entity.Role;
import com.oxytoca.app.entity.User;
import com.oxytoca.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/participant")
@PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
public class ParticipantController {
    @Autowired
    private UserRepository userRepository;

     @GetMapping
    public String userList(Model model) {
         var users = userRepository.findAll();
         model.addAttribute("users",
                 userRepository.findAll());
         return "participant-list";
    }
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user,
                               Model model) {
         model.addAttribute("user", user);
         model.addAttribute("roles", Role.values());
         return "user-edit";
    }

    @PostMapping("/saveEditUser")
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("id") User user) {
         user.setUsername(username);
         Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

         user.getRoles().clear();
        System.out.println("we are in user edit");
         for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
         }
         userRepository.save(user);
         return "redirect:/participant";
    }

}
