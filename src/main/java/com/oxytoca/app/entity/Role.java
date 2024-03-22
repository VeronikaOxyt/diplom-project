package com.oxytoca.app.entity;

import org.springframework.security.core.GrantedAuthority;
/**
 * Enum ролей.
 */
public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_REFEREE,
    ROLE_INSTRUCTOR;


    @Override
    public String getAuthority() {
        return name();
    }
}
