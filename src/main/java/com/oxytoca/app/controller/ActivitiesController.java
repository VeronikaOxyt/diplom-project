package com.oxytoca.app.controller;
import com.oxytoca.app.entity.User;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.app.repository.UserRepository;
import com.oxytoca.app.service.ActivityService;
import com.oxytoca.app.service.UserService;
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

/**
 * Контроллер мероприятий.
 *
 */

@Controller
public class ActivitiesController {
    private final ActivityRepository activityRepository;
    private final UserService userService;
    private final ActivityService activityService;
    private final UserRepository userRepository;

    public ActivitiesController(ActivityRepository activityRepository, UserService userService, ActivityService activityService, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userService = userService;
        this.activityService = activityService;
        this.userRepository = userRepository;
    }

    /**
     * Метод контроллера для отображения HTML-страницы приветствия.
     *
     */
    @GetMapping("/")
    public String greeting() {
        return "greeting";
    }

    /**
     * Метод контроллера для отображения HTML-страницы с доской мероприятий.
     */
    @GetMapping("/activitiesPoster")
    public String poster(Model model) {
        Iterable<Activity> allActivities = activityRepository.findAll();
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        model.addAttribute("allActs", allActivities);
        model.addAttribute("formatterOutput", formatterOutput);
        return "poster";
    }

    /**
     * Метод контроллера для отображения страницы с мероприятиями,
     * которые организовывает пользователь.
     * Доступен только пользователям с ролями "REFEREE" или "INSTRUCTOR".
     */
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

    /**
     * Метод контроллера для отображения страницы с мероприятиями,
     * в которых участвует пользователь.
     */
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

    /**
     * Метод контроллера для отображения формы добавления нового мероприятия.
     * Доступен только пользователям с ролями "REFEREE" или "INSTRUCTOR".
     * @param model Модель данных для передачи информации на страницу.
     * @return HTML-страница с формой добавления нового мероприятия.
     */
    @GetMapping("/addNewActivity")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String addNewActivity(Model model) {
        Activity activity = new Activity();
        model.addAttribute("activity", activity);
        return "activity-form";
    }

    /**
     * Метод контроллера для добавления нового мероприятия.
     * Доступен только пользователям с ролями "REFEREE" или "INSTRUCTOR".
     */
    @PostMapping("/saveNewActivity")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String saveNewActivity(@AuthenticationPrincipal User user,
                                  @RequestParam("file") MultipartFile file,
                                  @ModelAttribute("activity") @Valid Activity activity,
                                  BindingResult bindingResult,
                                  Model model) throws IOException {
        if (activity.getStart().isEmpty() || activity.getFinish().isEmpty()) {
            return "redirect:/activitiesPoster";
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ValidationController.getErrors(bindingResult);
            model.addAttribute("errors", errors);
            return "activity-form";
        } else {
            activityService.saveActivity(user, file, activity);
        }
        return "redirect:/activitiesPoster";
    }

    /**
     * Метод контроллера для фильтра доски мероприятий по тексту.
     */
    @PostMapping("/filter")
    public String filterByText(@RequestParam String text, Model model) {
        Iterable<Activity> activities;
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        if(text != null && !text.isEmpty()) {
            activities = activityRepository.findByText(text);
        } else {
            activities = activityRepository.findAll();
        }
        model.addAttribute("formatterOutput", formatterOutput);
        model.addAttribute("allActs", activities);
        return "poster";
    }

    /**
     * Метод контроллера для подписки пользователя на мероприятие.
     */
    @GetMapping("join/{activity}")
    public String joinActivity(@AuthenticationPrincipal User user,
                               @PathVariable Activity activity) {
        userService.joinActivity(user.getId(), activity);

        return "redirect:/activitiesPoster";
    }

    /**
     * Метод контроллера для отписки пользователя от мероприятия.
     */
    @GetMapping("disjoin/{activity}")
    public String disjoinActivity(@AuthenticationPrincipal User user,
                               @PathVariable Activity activity) {
        userService.disjoinActivity(user.getId(), activity.getId());

        return "redirect:/activitiesPoster";
    }

    /**
     * Метод контроллера для отображения формы редактирования мероприятия.
     * Помимо ограниччения по ролям доступен только создателю мероприятия.
     */
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

    /**
     * Метод контроллера для сохранения отредактированного мероприятия.
     */
    @PostMapping("/activity/saveEditActivity/{activity}")
    @PreAuthorize("hasRole('ROLE_REFEREE') || hasRole('ROLE_INSTRUCTOR')")
    public String saveEditActivity(
            @AuthenticationPrincipal User user,
            @RequestParam String type,
            @RequestParam String text,
            @RequestParam("file") MultipartFile file,
            @PathVariable Activity activity) throws IOException {
        if (type.isEmpty()) {
            type = activity.getType();
        }
        if (text.isEmpty()) {
            text = activity.getText();
        }
        if(Objects.equals(user.getId(), activity.getAuthor().getId()) &&
                !type.isEmpty() && !text.isEmpty()) {
            activityService.saveEditActivity(type, text,
                    file, activity);
        }
        return "redirect:/authorsActivities";
    }

    /**
     * Метод контроллера для удаления мероприятия.
     * Доступен только создателю мероприятия.
     */
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
