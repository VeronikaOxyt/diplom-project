package com.oxytoca.app.service;

import com.oxytoca.app.entity.Activity;
import com.oxytoca.app.entity.User;
import com.oxytoca.app.repository.ActivityRepository;
import com.oxytoca.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    private void saveActivityImg(MultipartFile file, Activity activity) throws IOException {
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

    public void saveActivity(User user, MultipartFile file, Activity activity) throws IOException {
        saveActivityImg(file, activity);
        activity.setAuthor(user);
        activityRepository.save(activity);
    }

    public void saveEditActivity(String type, String text,
                                 MultipartFile file, Activity activity) throws IOException {
        saveActivityImg(file, activity);
        activity.setType(type);
        activity.setText(text);
        activityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Activity activity) {
        if (!activity.getParticipants().isEmpty()) {
            for (User user : activity.getParticipants()) {
                userService.disjoinActivity(user.getId(), activity.getId());
            }
        }
        activityRepository.delete(activity);
    }

}
