package com.oxytoca.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация для настройки бина PasswordEncoder.
 */

@Configuration
public class EncoderConfig {
    /**
     * Создает и возвращает бин PasswordEncoder с использованием алгоритма BCrypt.
     * @return Бин PasswordEncoder.
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
