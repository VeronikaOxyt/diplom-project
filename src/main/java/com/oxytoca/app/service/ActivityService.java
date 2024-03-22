package com.oxytoca.app.service;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.entity.User;
import com.oxytoca.app.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс предоставляющий бизнес-логику и общий функционал по работе с объектами класса мероприятий.
 */
@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    private final UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    public ActivityService(ActivityRepository activityRepository, UserService userService) {
        this.activityRepository = activityRepository;
        this.userService = userService;
    }

    /**
     * Метод, сохраняющий в БД информацию о файле изображения
     * добавленном к мероприятиюю
     */
    void saveActivityImg(MultipartFile file, Activity activity) throws IOException {
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
    }

    /**
     * Метод, сохранющий в БД информацию о созданном мероприятии
     */
    public void saveActivity(User user, MultipartFile file, Activity activity) throws IOException {
        saveActivityImg(file, activity);
        activity.setAuthor(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        activity.setStartDateTime(LocalDateTime.parse(activity.getStart(), formatter));
        activity.setFinishDateTime(LocalDateTime.parse(activity.getFinish(), formatter));

        activityRepository.save(activity);
    }

    /**
     * Метод, сохранющий в БД информацию об отредактированном мероприятии
     */
    public void saveEditActivity(String type, String text,
                                 MultipartFile file, Activity activity) throws IOException {
        saveActivityImg(file, activity);
        activity.setType(type);
        activity.setText(text);
        activityRepository.save(activity);
    }

    /**
     * Метод, удаляющий всю из БД информацию о  мероприятии (фактически удаление мероприятия)
     */
    @Transactional
    public void deleteActivity(Activity activity) {
        if (!activity.getParticipants().isEmpty()) {
            for (User user : activity.getParticipants()) {
                userService.disjoinActivity(user.getId(), activity.getId());
            }
        }
        activityRepository.delete(activity);
    }

    public String getUploadPath() {
        return uploadPath;
    }
}
