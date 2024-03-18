package com.oxytoca.app.controller;
import com.oxytoca.app.entity.User;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.app.repository.UserRepository;
import com.oxytoca.app.service.ActivityService;
import com.oxytoca.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@Controller
public class ActivitiesController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String greeting() {
        return "greeting";
    }

    @GetMapping("/poster")
    public String poster(Model model) {
        Iterable<Activity> allActivities = activityRepository.findAll();
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        model.addAttribute("allActs", allActivities);
        model.addAttribute("formatterOutput", formatterOutput);
        return "poster";
    }

    @GetMapping("/authorsActivities")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String authorsActivities(@AuthenticationPrincipal User user,
                                    Model model) {
        Iterable<Activity> allActivities = userRepository
                .findUserById(user.getId())
                .getAuthorsActivities();
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

        model.addAttribute("allActs", allActivities);
        model.addAttribute("userId", user.getId());
        model.addAttribute("formatterOutput", formatterOutput);
        return "poster";
    }

    @GetMapping("/joiningActivities")
    public String joiningActivities(@AuthenticationPrincipal User user,
                                    Model model) {
        Iterable<Activity> allActivities = userRepository
                .findUserById(user.getId()).getMyActivities();
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        model.addAttribute("allActs", allActivities);
        model.addAttribute("formatterOutput", formatterOutput);
        return "poster";
    }

    @GetMapping("/addNewActivity")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String addNewActivity(Model model) {
        Activity activity = new Activity();
        model.addAttribute("activity", activity);
        return "activity-form";
    }

    @PostMapping("/saveNewActivity")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String saveNewActivity(@AuthenticationPrincipal User user,
                               @RequestParam("file") MultipartFile file,
                               @ModelAttribute("start") String start,
                               @ModelAttribute("finish") String finish,
                               @ModelAttribute("activity") @Valid Activity activity,
                                BindingResult bindingResult,
                               Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ValidationController.getErrors(bindingResult);
            model.addAttribute("errors", errors);
            return "activity-form";
        } else {
            activityService.saveActivity(user, file, activity);
        }
        return "redirect:/poster";
    }
    @PostMapping("filter")
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

    @GetMapping("join/{activity}")
    public String joinActivity(@AuthenticationPrincipal User user,
                               @PathVariable Activity activity) {
        userService.joinActivity(user.getId(), activity);

        return "redirect:/poster";
    }

    @GetMapping("disjoin/{activity}")
    public String disjoinActivity(@AuthenticationPrincipal User user,
                               @PathVariable Activity activity) {
        userService.disjoinActivity(user.getId(), activity.getId());

        return "redirect:/poster";
    }

    @GetMapping("/activity/{activity}")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String editActivity(@PathVariable Activity activity,
                               @AuthenticationPrincipal User user,
                               Model model) {
        if(Objects.equals(user.getId(), activity.getAuthor().getId())) {
            model.addAttribute("activity", activity);
            return "activity-edit";
        }
        return "poster";
    }

    @PostMapping("/activity/saveEditActivity")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String saveEditActivity(
            @AuthenticationPrincipal User user,
            @RequestParam String type,
            @RequestParam String text,
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") Activity activity) throws IOException {
        if(Objects.equals(user.getId(), activity.getAuthor().getId())) {
            activityService.saveEditActivity(type, text,
                    file, activity);
        }
        return "redirect:/authorsActivities";
    }

    @GetMapping("/delete/{activity}")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String deleteActivity(
            @AuthenticationPrincipal User user,
            @PathVariable Activity activity) throws IOException {
        if(Objects.equals(user.getId(), activity.getAuthor().getId())) {
            activityRepository.delete(activity);
        }
        return "redirect:/authorsActivities";
    }
}
