package com.oxytoca.app.repository;

import com.oxytoca.app.entity.Activity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
    List<Activity> findByType(String type);

    Activity findActivityById(Long id);

}
