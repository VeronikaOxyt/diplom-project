package com.oxytoca.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationControllerTest {
    @Test
    void testGetErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = List.of(
                new FieldError("objectName", "field1", "Error message 1"),
                new FieldError("objectName", "field2", "Error message 2")
        );
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        Map<String, String> errors = ValidationController.getErrors(bindingResult);

        assertEquals("Error message 1", errors.get("field1Error"));
        assertEquals("Error message 2", errors.get("field2Error"));
    }
}
