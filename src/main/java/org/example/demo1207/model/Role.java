package org.example.demo1207.model;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_MODERATOR;

    @Override
    public String toString() {
        return name();
    }
}
