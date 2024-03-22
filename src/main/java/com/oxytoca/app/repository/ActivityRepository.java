package com.oxytoca.app.repository;

import com.oxytoca.app.entity.Activity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * Интерфейс для выполнения операций CRUD с объектами типа Activity
 */
public interface ActivityRepository extends CrudRepository<Activity, Long> {
    List<Activity> findByText(String text);

    Activity findActivityById(Long id);

}
