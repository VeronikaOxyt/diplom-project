package com.oxytoca.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Контроллер валидации вводимих в формы значений.
 */
@Controller
public class ValidationController {
    /**
     *
     * Метод контроллера для получения информации в виде мапы об ошибках валидации объекта,
     * обнаруженных в процессе проверки данных введенных в HTML-формы.
     * Результаты валидации хранятся в объекте класса BindingResult.
     * Метод преобразует объект BindingResult в Map, где ключами являются имена полей,
     * содержащих ошибки валидации, а значениями являются сами ошибки (сообщения об ошибках)
     * Это позволяет легко извлечь информацию об ошибках и отобразить пользователю  на веб-странице.
     *
     */
    static Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream()
                .collect(collector);
    }
}
