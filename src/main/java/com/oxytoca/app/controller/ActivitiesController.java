package com.oxytoca.app.controller;
import com.oxytoca.registration.entity.User;
import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ActivitiesController {
    @Autowired
    private ActivityRepository activityRepository;

    @GetMapping("/")
    public String greeting() {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Model model) {
        Iterable<Activity> allActivities = activityRepository.findAll();
        System.out.println(allActivities);
        model.addAttribute("allActs", allActivities);
        return "main";
    }

    @GetMapping("/addNewActivity")
    public String addActivity(Model model) {
        Activity activity = new Activity();
        model.addAttribute("activity", activity);
        return "activity-form";
    }

    @PostMapping("/saveActivity")
    public String saveActivity(@AuthenticationPrincipal User user,
                               @ModelAttribute("activity") Activity activity) {
        activity.setAuthor(user);
        activityRepository.save(activity);
        return "redirect:/main";
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
        return "main";
    }
}
