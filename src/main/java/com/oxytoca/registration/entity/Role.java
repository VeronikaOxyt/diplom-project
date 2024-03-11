package com.oxytoca.registration.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_REFEREE,
    ROLE_INSTRUCTOR;


    @Override
    public String getAuthority() {
        return name();
    }
}
