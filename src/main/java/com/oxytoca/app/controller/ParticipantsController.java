package com.oxytoca.app.controller;

import com.oxytoca.app.entity.Activity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * Контроллер участников мероприятий.
 *
 */
@Controller
@RequestMapping("/activityParticipants")
public class ParticipantsController {
    /**
     * Метод контроллера для отображения HTML-страницы со списком участников мероприятий.
     */
    @GetMapping("/{activity}")
    public String getParticipantList(@PathVariable Activity activity, Model model) {
        model.addAttribute(activity);
        return "participant-list";
    }
}
