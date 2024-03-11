package com.oxytoca.app.controller;
import com.oxytoca.registration.entity.User;
import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Controller
public class ActivitiesController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping(path ="/")
    public String greeting() {
        return "greeting";
    }

    @GetMapping(path ="/poster")
    public String poster(Model model) {
        Iterable<Activity> allActivities = activityRepository.findAll();
        model.addAttribute("allActs", allActivities);
        return "poster";
    }

    @GetMapping(path ="/addNewActivity")
    public String addActivity(Model model) {
        Activity activity = new Activity();
        model.addAttribute("activity", activity);
        return "activity-form";
    }

    @PostMapping(path ="/saveActivity")
    public String saveActivity(@AuthenticationPrincipal User user,
                               @RequestParam("file") MultipartFile file,
                               @ModelAttribute("activity") Activity activity) throws IOException {
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidIfFile = UUID.randomUUID().toString();
            String resultFilename = uuidIfFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));
            activity.setFilename(resultFilename);
        }
        activity.setAuthor(user);
        activityRepository.save(activity);
        return "redirect:/poster";
    }
    @PostMapping(path = "filter")
    public String filterByType(@RequestParam String type, Model model) {
        Iterable<Activity> activities;
        if(type != null && !type.isEmpty()) {
            activities = activityRepository.findByType(type);
        } else {
            activities = activityRepository.findAll();
        }
        model.addAttribute("allActs", activities);
        return "poster";
    }


    @GetMapping(path ="join/{activity}")
    public String joinActivity(@AuthenticationPrincipal User user,
                               @PathVariable Activity activity) {
        System.out.println("user id " + user.getId());
        System.out.println("activity id " + activity.getId());
        userService.joinActivity(user.getId(), activity);

        return "redirect:/poster";
    }
    @GetMapping(path ="disjoin/{activity}")
    public String disjoinActivity(@AuthenticationPrincipal User user,
                               @PathVariable Activity activity) {
        System.out.println("user id " + user.getId());
        System.out.println("activity id " + activity.getId());
        userService.disjoinActivity(user.getId(), activity);

        return "redirect:/poster";
    }

}
